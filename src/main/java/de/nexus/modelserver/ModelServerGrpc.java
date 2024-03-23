package de.nexus.modelserver;

import de.nexus.modelserver.proto.*;
import hipe.engine.message.production.ProductionResult;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ModelServerGrpc {
    private final static int GRPC_PORT = 9090;
    private final ModelServer modelServer;
    private final Server grpcServer;

    public ModelServerGrpc(ModelServer modelServer) {
        this.modelServer = modelServer;
        this.grpcServer = ServerBuilder
                .forPort(GRPC_PORT)
                .addService(new ModelServerManagement(this))
                .addService(new ModelServerPattern(this))
                .addService(new ModelServerConstraints(this))
                .addService(new ModelServerEdits(this))
                .build();
    }

    public void start() throws IOException, InterruptedException {
        System.out.println("[gRPC Server] Server starting...");
        this.grpcServer.start();
        System.out.println("[gRPC Server] Server started, listening on " + GRPC_PORT);
        this.grpcServer.awaitTermination();
    }

    public void stop() throws InterruptedException {
        System.out.println("[gRPC Server] Server stopping...");
        this.grpcServer.shutdown();
        this.grpcServer.awaitTermination(1, TimeUnit.MINUTES);
        this.grpcServer.shutdownNow();
        System.out.println("[gRPC Server] Server stopped!");
    }

    private static class ModelServerManagement extends ModelServerManagementGrpc.ModelServerManagementImplBase {
        private final ModelServerGrpc grpcHandler;

        public ModelServerManagement(ModelServerGrpc handler) {
            this.grpcHandler = handler;
        }

        @Override
        public void terminateServer(de.nexus.modelserver.proto.ModelServerManagement.TerminateServerRequest request, StreamObserver<de.nexus.modelserver.proto.ModelServerManagement.TerminateServerResponse> responseObserver) {
            System.out.println("[gRPC Server | ModelServerManagement] terminateServer");
            this.grpcHandler.modelServer.terminateEngine();
            responseObserver.onNext(de.nexus.modelserver.proto.ModelServerManagement.TerminateServerResponse.getDefaultInstance());
            responseObserver.onCompleted();
            try {
                this.grpcHandler.stop();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void getState(de.nexus.modelserver.proto.ModelServerManagement.GetStateRequest request, StreamObserver<de.nexus.modelserver.proto.ModelServerManagement.GetStateResponse> responseObserver) {
            System.out.println("[ModelServer] TEST - Extract Data by request...");
            Map<String, ProductionResult> extractData = this.grpcHandler.modelServer.extractData();
            extractData.forEach((x, y) -> {
                System.out.printf("=== New Matches (%d) ===%n", y.getNewMatches().size());
                y.getNewMatches().forEach(System.out::println);
                System.out.printf("=== Deleted Matches (%d) ===%n", y.getDeleteMatches().size());
                y.getDeleteMatches().forEach(System.out::println);
            });
            responseObserver.onNext(de.nexus.modelserver.proto.ModelServerManagement.GetStateResponse.getDefaultInstance());
            responseObserver.onCompleted();
        }

        @Override
        public void exportModel(de.nexus.modelserver.proto.ModelServerManagement.ExportModelRequest request, StreamObserver<de.nexus.modelserver.proto.ModelServerManagement.ExportModelResponse> responseObserver) {
            Path modelPath = Path.of(this.grpcHandler.modelServer.getConfiguration().getModelPath());
            Path basePath = modelPath.getParent();

            if (request.hasExportPath()) {
                try {
                    basePath = Path.of(request.getExportPath().getValue());
                } catch (InvalidPathException ex) {
                    responseObserver.onNext(de.nexus.modelserver.proto.ModelServerManagement.ExportModelResponse.newBuilder().setSuccess(false).setMessage("Unable to resolve given path: " + request.getExportPath().getValue()).build());
                    responseObserver.onCompleted();
                    return;
                }
            }

            if (!basePath.toFile().exists()) {
                responseObserver.onNext(de.nexus.modelserver.proto.ModelServerManagement.ExportModelResponse.newBuilder().setSuccess(false).setMessage("Unable to locate: " + basePath).build());
                responseObserver.onCompleted();
                return;
            }
            if (!basePath.toFile().canWrite()) {
                responseObserver.onNext(de.nexus.modelserver.proto.ModelServerManagement.ExportModelResponse.newBuilder().setSuccess(false).setMessage("Unable to write into: " + basePath).build());
                responseObserver.onCompleted();
                return;
            }

            String modelName = modelPath.getFileName().toString();
            String targetModelName = modelName.endsWith(".export.xmi") ? modelName : modelName.replace(".xmi", ".export.xmi");
            Path targetPath = basePath.resolve(targetModelName);
            if (request.hasExportName()) {
                try {
                    targetPath = basePath.resolve(request.getExportName().getValue());
                } catch (InvalidPathException ex) {
                    responseObserver.onNext(de.nexus.modelserver.proto.ModelServerManagement.ExportModelResponse.newBuilder().setSuccess(false).setMessage(String.format("Unable to resolve export target path: %s in base directory: %s", request.getExportName().getValue(), basePath)).build());
                    responseObserver.onCompleted();
                    return;
                }
            }

            boolean res = this.grpcHandler.modelServer.getEmfLoader().exportModel(targetPath, request.getExportWithIds());
            responseObserver.onNext(de.nexus.modelserver.proto.ModelServerManagement.ExportModelResponse.newBuilder().setSuccess(res).setMessage("").setExportedPath(targetPath.toString()).build());
            responseObserver.onCompleted();
        }
    }

    private static class ModelServerPattern extends ModelServerPatternGrpc.ModelServerPatternImplBase {
        private final ModelServerGrpc grpcHandler;

        public ModelServerPattern(ModelServerGrpc handler) {
            this.grpcHandler = handler;
        }

        @Override
        public void getPatterns(ModelServerPatterns.GetPatternsRequest request, StreamObserver<ModelServerPatterns.GetPatternsResponse> responseObserver) {
            Map<String, ProductionResult> extractData = this.grpcHandler.modelServer.extractData();
            grpcHandler.modelServer.getPatternRegistry().processHiPE(extractData);

            List<ModelServerPatterns.Pattern> patterns = grpcHandler.modelServer.getPatternRegistry().getPatterns().values().stream().map(ProtoMapper::map).toList();

            responseObserver.onNext(ModelServerPatterns.GetPatternsResponse.newBuilder().addAllPatterns(patterns).build());
            responseObserver.onCompleted();
        }

        @Override
        public void getPattern(ModelServerPatterns.GetPatternRequest request, StreamObserver<ModelServerPatterns.GetPatternResponse> responseObserver) {
            Map<String, ProductionResult> extractData = this.grpcHandler.modelServer.extractData();
            grpcHandler.modelServer.getPatternRegistry().processHiPE(extractData);

            ModelServerPatterns.Pattern pattern = ProtoMapper.map(grpcHandler.modelServer.getPatternRegistry().getPattern(request.getPatternName()));

            responseObserver.onNext(ModelServerPatterns.GetPatternResponse.newBuilder().setPattern(pattern).build());
            responseObserver.onCompleted();
        }

        @Override
        public void listPatterns(ModelServerPatterns.ListPatternsRequest request, StreamObserver<ModelServerPatterns.ListPatternsResponse> responseObserver) {
            responseObserver.onNext(ModelServerPatterns.ListPatternsResponse.newBuilder().addAllPatterns(this.grpcHandler.modelServer.getPatternRegistry().getPatterns().keySet()).build());
            responseObserver.onCompleted();
        }
    }

    private static class ModelServerConstraints extends ModelServerConstraintsGrpc.ModelServerConstraintsImplBase {
        private final ModelServerGrpc grpcHandler;

        public ModelServerConstraints(ModelServerGrpc handler) {
            this.grpcHandler = handler;
        }

        @Override
        public void getConstraints(de.nexus.modelserver.proto.ModelServerConstraints.GetConstraintsRequest request, StreamObserver<de.nexus.modelserver.proto.ModelServerConstraints.GetConstraintsResponse> responseObserver) {
            Map<String, ProductionResult> extractData = this.grpcHandler.modelServer.extractData();
            grpcHandler.modelServer.getPatternRegistry().processHiPE(extractData);

            grpcHandler.modelServer.getConstraintRegistry().getConstraints().values().forEach(constraint -> {
                constraint.evaluate(grpcHandler.modelServer.getPatternRegistry());
                constraint.computeProposals(grpcHandler.modelServer.getEmfLoader());
            });

            List<de.nexus.modelserver.proto.ModelServerConstraints.Constraint> constraints = grpcHandler.modelServer.getConstraintRegistry().getConstraints().values().stream().map(x -> ProtoMapper.map(x, this.grpcHandler.modelServer.getEmfLoader())).toList();

            responseObserver.onNext(de.nexus.modelserver.proto.ModelServerConstraints.GetConstraintsResponse.newBuilder().addAllConstraints(constraints).build());
            responseObserver.onCompleted();
        }

        @Override
        public void getConstraint(de.nexus.modelserver.proto.ModelServerConstraints.GetConstraintRequest request, StreamObserver<de.nexus.modelserver.proto.ModelServerConstraints.GetConstraintResponse> responseObserver) {
            Map<String, ProductionResult> extractData = this.grpcHandler.modelServer.extractData();
            grpcHandler.modelServer.getPatternRegistry().processHiPE(extractData);

            AbstractConstraint constraint = grpcHandler.modelServer.getConstraintRegistry().getConstraint(request.getConstraintName());
            constraint.evaluate(grpcHandler.modelServer.getPatternRegistry());
            constraint.computeProposals(grpcHandler.modelServer.getEmfLoader());

            de.nexus.modelserver.proto.ModelServerConstraints.Constraint protoConstraint = ProtoMapper.map(constraint, this.grpcHandler.modelServer.getEmfLoader());

            responseObserver.onNext(de.nexus.modelserver.proto.ModelServerConstraints.GetConstraintResponse.newBuilder().setConstraint(protoConstraint).build());
            responseObserver.onCompleted();
        }

        @Override
        public void listConstraints(de.nexus.modelserver.proto.ModelServerConstraints.ListConstraintsRequest request, StreamObserver<de.nexus.modelserver.proto.ModelServerConstraints.ListConstraintsResponse> responseObserver) {
            responseObserver.onNext(de.nexus.modelserver.proto.ModelServerConstraints.ListConstraintsResponse.newBuilder().addAllConstraints(this.grpcHandler.modelServer.getConstraintRegistry().getConstraints().keySet()).build());
            responseObserver.onCompleted();
        }
    }

    private static class ModelServerEdits extends ModelServerEditsGrpc.ModelServerEditsImplBase {
        private final ModelServerGrpc grpcHandler;

        public ModelServerEdits(ModelServerGrpc handler) {
            this.grpcHandler = handler;
        }

        @Override
        public void requestEdit(de.nexus.modelserver.proto.ModelServerEdits.PostEditRequest request, StreamObserver<de.nexus.modelserver.proto.ModelServerEdits.PostEditResponse> responseObserver) {
            switch (request.getRequestCase()) {
                case EDIT -> {
                    ModelServerEditStatements.EditResponse response = this.grpcHandler.modelServer.getEditProcessor().process(request.getEdit());
                    responseObserver.onNext(de.nexus.modelserver.proto.ModelServerEdits.PostEditResponse.newBuilder().setEdit(response).build());
                }
                case EDITCHAIN -> {
                    ModelServerEditStatements.EditChainResponse chainResponse = this.grpcHandler.modelServer.getEditProcessor().process(request.getEditChain());
                    responseObserver.onNext(de.nexus.modelserver.proto.ModelServerEdits.PostEditResponse.newBuilder().setEditChain(chainResponse).build());
                }
                case REQUEST_NOT_SET -> throw new IllegalArgumentException("Request not set!");
            }
            ;
            responseObserver.onCompleted();
        }
    }
}
