package main.java.ru.clevertec.check.exceptions;

public class NotEnoughMoneyException extends Exception{
    public NotEnoughMoneyException(String message) {
        super(message);
    }

}