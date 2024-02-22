package de.nexus.modelserver;

import de.nexus.modelserver.proto.ModelServerConstraintsGrpc;
import de.nexus.modelserver.proto.ModelServerManagementGrpc;
import de.nexus.modelserver.proto.ModelServerPatternGrpc;
import de.nexus.modelserver.proto.ModelServerPatterns;
import hipe.engine.message.production.ProductionResult;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
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
                constraint.computeProposals(grpcHandler.modelServer.getPatternRegistry());
            });

            List<de.nexus.modelserver.proto.ModelServerConstraints.Constraint> constraints = grpcHandler.modelServer.getConstraintRegistry().getConstraints().values().stream().map(ProtoMapper::map).toList();

            responseObserver.onNext(de.nexus.modelserver.proto.ModelServerConstraints.GetConstraintsResponse.newBuilder().addAllConstraints(constraints).build());
            responseObserver.onCompleted();
        }

        @Override
        public void getConstraint(de.nexus.modelserver.proto.ModelServerConstraints.GetConstraintRequest request, StreamObserver<de.nexus.modelserver.proto.ModelServerConstraints.GetConstraintResponse> responseObserver) {
            Map<String, ProductionResult> extractData = this.grpcHandler.modelServer.extractData();
            grpcHandler.modelServer.getPatternRegistry().processHiPE(extractData);

            AbstractConstraint constraint = grpcHandler.modelServer.getConstraintRegistry().getConstraint(request.getConstraintName());
            constraint.evaluate(grpcHandler.modelServer.getPatternRegistry());
            constraint.computeProposals(grpcHandler.modelServer.getPatternRegistry());

            de.nexus.modelserver.proto.ModelServerConstraints.Constraint protoConstraint = ProtoMapper.map(constraint);

            responseObserver.onNext(de.nexus.modelserver.proto.ModelServerConstraints.GetConstraintResponse.newBuilder().setConstraint(protoConstraint).build());
            responseObserver.onCompleted();
        }

        @Override
        public void listConstraints(de.nexus.modelserver.proto.ModelServerConstraints.ListConstraintsRequest request, StreamObserver<de.nexus.modelserver.proto.ModelServerConstraints.ListConstraintsResponse> responseObserver) {
            responseObserver.onNext(de.nexus.modelserver.proto.ModelServerConstraints.ListConstraintsResponse.newBuilder().addAllConstraints(this.grpcHandler.modelServer.getConstraintRegistry().getConstraints().keySet()).build());
            responseObserver.onCompleted();
        }
    }
}
