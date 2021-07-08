package ru.netology.cloud_service_app.aspects;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.netology.cloud_service_app.models.FileData;
import ru.netology.cloud_service_app.models.NewFilename;

import javax.validation.ConstraintViolationException;
import java.security.Principal;
import java.util.List;

@Slf4j
@Component
@Aspect
public class LoggingAspect {
    // Вывод всех файлов

    @Before(value = "callFileControllerGetAllFilesMethodAdvice(principal)", argNames = "principal")
    public void FileControllerGetAllFilesBeforeMethodAdvice(Principal principal) {
        log.info(String.format("Принят запрос от пользоваетеля %s на просмотр всех файлов", principal.getName()));
    }

    @AfterReturning(pointcut = "callFileControllerGetAllFilesMethodAdvice(principal)", argNames = "principal,filesList", returning = "filesList")
    public void FileControllerGetAllFilesAfterMethodAdvice(Principal principal, ResponseEntity<List<FileData>> filesList) {
        log.info(String.format("Пользователь %s получил список файлов: %s", principal.getName(), filesList.getBody()));
    }

    @Pointcut("execution(* ru.netology.cloud_service_app.controllers.FileController.getAllFiles(..)) && args(.., principal))")
    public void callFileControllerGetAllFilesMethodAdvice(Principal principal) {
    }

    // Сохранение файла

    @Pointcut("execution(* ru.netology.cloud_service_app.handlers.file_uploaders.FileHandler.uploadToDb(..))")
    public void callFileUploaderUploadToDbMethodAdvice() {
    }

    @AfterThrowing(pointcut = "callFileUploaderUploadToDbMethodAdvice()", throwing = "exception")
    public void fileUploaderUploadToDbAfterThrowingMethodAdvice(Throwable exception) {
        log.error(exception.getMessage());
    }

    @Pointcut("execution(* ru.netology.cloud_service_app.controllers.FileController.saveFile(..)) && args(.., principal))")
    public void callFileControllerSaveFileMethodAdvice(Principal principal) {
    }


    @Before(value = "callFileControllerSaveFileMethodAdvice(principal)", argNames = "principal")
    public void FileControllerSaveFileBeforeMethodAdvice(Principal principal) {
        log.info(String.format("Принят запрос от пользоваетеля %s на сохранение файла", principal.getName()));
    }

    @AfterReturning(value = "callFileControllerSaveFileMethodAdvice(principal)", argNames = "principal")
    public void fileControllerSaveFileAfterMethodAdvice(Principal principal) {
        log.info(String.format("Пользователь %s успешно сохранил файл", principal.getName()));
    }

    @AfterThrowing(pointcut = "callCloudServiceUserRepositoryMethodAdvice()", throwing = "exception")
    public void cloudServiceUserRepositoryAfterThrowingMethodAdvice(Throwable exception) {
        log.error(exception.getMessage());
    }

    @Pointcut("execution(* ru.netology.cloud_service_app.repositories.user_repositories.CloudServiceUserRepository.findUserByLogin(..))")
    public void callCloudServiceUserRepositoryMethodAdvice() {
    }


    @Pointcut(value = "execution(* ru.netology.cloud_service_app.handlers.dbhandlers.DbHandler.isFileAbleToBeRestored(..))")
    public void callDbHandlerCheckFileExistsButHasDeletedStatusMethodAdvice() {
    }

    @SneakyThrows
    @Around(value = "callDbHandlerCheckFileExistsButHasDeletedStatusMethodAdvice()")
    public Object defaultFileUploaderCheckFileExistsButHasDeletedStatusAfterMethodAdvice(ProceedingJoinPoint proceedingJoinPoint) {
        log.info("Осуществляется проверка, возможно ли восстановить удаленный файл");
        final Boolean isFileExists = (Boolean) proceedingJoinPoint.proceed();
        if (isFileExists) {
            log.info("Файл восстановлен, его статус обновился");
        } else {
            log.info("В БД не обнаружено файлов, пригодных для восстановления");
        }
        return isFileExists;
    }

    // Удаление файла
    @Pointcut(value = "execution(* ru.netology.cloud_service_app.controllers.FileController.deleteFile(..)) && args(filename, principal))", argNames = "filename,principal")
    public void callFileControllerDeleteFileMethodAdvice(String filename, Principal principal) {
    }

    @Before(value = "callFileControllerDeleteFileMethodAdvice(filename,principal)", argNames = "filename,principal")
    public void fileControllerDeleteFileBeforeMethodAdvice(String filename, Principal principal) {
        log.info(String.format("Принят запрос от пользоваетеля %s на удаление файла %s", principal.getName(), filename));
    }

    @AfterReturning(value = "callFileControllerDeleteFileMethodAdvice(filename,principal)", argNames = "filename,principal")
    public void fileControllerDeleteFileAfterReturningMethodAdvice(String filename, Principal principal) {
        log.info(String.format("Пользоваетель %s успешно удалил файл %s", principal.getName(), filename));
    }

    @Pointcut(value = "execution(* ru.netology.cloud_service_app.repositories.file_repositories.CloudServiceFileRepository.deleteFile(..))")
    public void callCloudServiceFileRepositoryDeleteFileMethodAdvice() {
    }

    @AfterThrowing(pointcut = "callCloudServiceFileRepositoryDeleteFileMethodAdvice()", throwing = "exception")
    public void cloudServiceFileRepositoryDeleteFileAfterThrowingMethodAdvice(Throwable exception) {
        log.error(exception.getMessage());
    }

    // Изменение файла
    @Pointcut(value = "execution(* ru.netology.cloud_service_app.controllers.FileController.editFilename(..)) && args(oldFilename, newFilename, principal))", argNames = "oldFilename, newFilename, principal")
    public void callFileControllerEditFilenameMethodAdvice(String oldFilename, NewFilename newFilename, Principal principal) {
    }

    @Before(value = "callFileControllerEditFilenameMethodAdvice(oldFilename, newFilename, principal)", argNames = "oldFilename, newFilename, principal")
    public void fileControllerEditFilenameBeforeMethodAdvice(String oldFilename, NewFilename newFilename, Principal principal) {
        log.info(String.format("Принят запрос от пользователя %s на изменение названия файла %s, на новое: %s", principal.getName(), oldFilename, newFilename.getFilename()));
    }

    @AfterThrowing(value = "callFileControllerEditFilenameMethodAdvice(oldFilename, newFilename, principal)", argNames = "oldFilename, newFilename, principal,exception", throwing = "exception")
    public void fileControllerEditFilenameBeforeMethodAdvice(String oldFilename, NewFilename newFilename, Principal principal, Throwable exception) {
        log.info(String.format("Запрос на изменение файла %s отклонен. Невалидные данные. Причина: %s", oldFilename, exception.getMessage()));
    }

    @AfterReturning(value = "callFileControllerEditFilenameMethodAdvice(oldFilename, newFilename, principal)", argNames = "oldFilename, newFilename, principal")
    public void fileControllerEditFilenameAfterReturningMethodAdvice(String oldFilename, NewFilename newFilename, Principal principal) {
        log.info(String.format("Пользоваетель %s успешно изменил файл %s, новое название файла: %s", principal.getName(), oldFilename, newFilename.getFilename()));
    }

    @Pointcut(value = "execution(* ru.netology.cloud_service_app.repositories.file_repositories.CloudServiceFileRepository.editFilename(..))")
    public void callCloudServiceFileRepositoryEditFilenameMethodAdvice() {
    }

    @AfterThrowing(pointcut = "callCloudServiceFileRepositoryEditFilenameMethodAdvice()", throwing = "exception")
    public void cloudServiceFileRepositoryEditFilenameAfterThrowingMethodAdvice(Throwable exception) {
        log.error(exception.getMessage());
    }

    // Загрузка файла с сервера
    @Pointcut(value = "execution(* ru.netology.cloud_service_app.repositories.file_repositories.CloudServiceFileRepository.downloadFile(..)))")
    public void callCloudServiceFileRepositoryDownloadFileMethodAdvice() {
    }

    @AfterThrowing(pointcut = "callCloudServiceFileRepositoryDownloadFileMethodAdvice()", throwing = "exception", argNames = "exception")
    public void callCloudServiceFileRepositoryDownloadFileAfterThrowingMethodAdvice(Throwable exception) {
        log.error("Ошибка загрузки файла. " + exception.getMessage());
    }
}
