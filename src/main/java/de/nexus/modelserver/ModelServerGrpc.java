package de.nexus.modelserver;

import de.nexus.modelserver.proto.ModelServerManagementGrpc;
import hipe.engine.message.production.ProductionResult;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
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
            Map<String, ProductionResult> extractData;

            try {
                extractData = this.grpcHandler.modelServer.extractData();
                extractData.forEach((x, y) -> {
                    System.out.printf("=== New Matches (%d) ===%n", y.getNewMatches().size());
                    y.getNewMatches().forEach(System.out::println);
                    System.out.printf("=== Deleted Matches (%d) ===%n", y.getDeleteMatches().size());
                    y.getDeleteMatches().forEach(System.out::println);
                });
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            responseObserver.onNext(de.nexus.modelserver.proto.ModelServerManagement.GetStateResponse.getDefaultInstance());
            responseObserver.onCompleted();
        }
    }
}
