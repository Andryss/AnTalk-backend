package ru.andryss.antalk.server.exception;

/**
 * Класс, описывающий все ошибки, возникающие в приложении
 */
public class Errors {
    /**
     * Неожиданная неотловленная ошибка
     */
    public static BaseException unhandledExceptionError() {
        return new BaseException(500, "internal.error", "Что-то пошло не так...");
    }

    /**
     * Пользователь не найден
     */
    public static BaseException userNotFound(long userId) {
        return new BaseException(404, "user.absent.error",
                String.format("Пользователь с id=\"%s\" не найден", userId));
    }

    /**
     * Сессия не найдена
     */
    public static BaseException sessionNotFound(long sessionId) {
        return new BaseException(404, "session.absent.error",
                String.format("Сессия с id=\"%s\" не найден", sessionId));
    }

    /**
     * Чат не найден
     */
    public static BaseException chatNotFound(long chatId) {
        return new BaseException(404, "chat.absent.error",
                String.format("Чат с id=\"%s\" не найден", chatId));
    }

    /**
     * Сообщение не найдено
     */
    public static BaseException messageNotFound(long messageId) {
        return new BaseException(404, "message.absent.error",
                String.format("Сообщение с id=\"%s\" не найдено", messageId));
    }

    /**
     * Обновление не найдено
     */
    public static BaseException updateNotFound(long updateId) {
        return new BaseException(404, "update.absent.error",
                String.format("Обновление с id=\"%s\" не найдено", updateId));
    }

    /**
     * Уведомление не найдено
     */
    public static BaseException notificationNotFound(long notificationId) {
        return new BaseException(404, "notification.absent.error",
                String.format("Уведомление с id=\"%s\" не найдено", notificationId));
    }

    /**
     * Неверное имя пользователя или пароль
     */
    public static BaseException unauthorized() {
        return new BaseException(401, "user.unauthorized", "Неверный логин или пароль");
    }
}
