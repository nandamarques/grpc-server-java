package br.com.grpc.service;

import java.util.Optional;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Service;

import br.com.grpc.GrpcApplication;
import br.com.grpc.dao.UserDao;
import br.com.grpc.generated.APIResponse;
import br.com.grpc.generated.Empty;
import br.com.grpc.generated.LoginRequest;
import br.com.grpc.generated.RegisterRequest;
import br.com.grpc.generated.UserGrpc;
import br.com.grpc.model.User;
import io.grpc.stub.StreamObserver;

@Service
public class UserService extends UserGrpc.UserImplBase {

    @Autowired
    private UserDao userDao;

    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    @Transactional
    @Override
    public void login(LoginRequest request, StreamObserver<APIResponse> responseObserver) {
        String username = request.getUsername();
        String password = request.getPassword();

        APIResponse.Builder response = APIResponse.newBuilder();

        Optional<User> userFound = userDao.findByUsername(username);

        if (userFound.isPresent()) {
            User user = userFound.get();
    
            if (userFound.get().getPassword().trim().equals(password.trim())) {
                response.setResponseMessage("Sucesso")
                        .setResponseCode(200);
            } else {
                response.setResponseMessage("Senha incorreta.")
                        .setResponseCode(409);
            }
        } else {
            response.setResponseMessage("Username não encontrado.")
                    .setResponseCode(404);
        }


        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void logout(Empty request, StreamObserver<APIResponse> responseObserver) {
        // A FAZER
    }

    @Transactional
    @Override
    public void register(RegisterRequest request, StreamObserver<APIResponse> responseObserver) {
        if (userDao == null) {
            logger.info("UserDao is null in register!");
        } else {
            logger.info("UserDao is NOT NULL in register!");
        }

        Optional<User> existingUser = userDao.findByUsername(request.getUsername());
        APIResponse.Builder response = APIResponse.newBuilder();

        if (existingUser.isPresent()) {
            response.setResponseMessage("Usuário já existente!")
                    .setResponseCode(409);

            responseObserver.onNext(response.build());
            responseObserver.onCompleted();
            return;
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setCpf(request.getCpf());
        user.setPassword(request.getPassword());

        userDao.save(user);

        response.setResponseMessage("Sucesso")
                .setResponseCode(200);

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();

    }

}
