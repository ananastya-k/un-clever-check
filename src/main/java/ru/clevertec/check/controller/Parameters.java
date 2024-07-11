package main.java.ru.clevertec.check.controller;

import main.java.ru.clevertec.check.cli.parser.CommandLineParser;
import main.java.ru.clevertec.check.cli.parser.Parameter;
import main.java.ru.clevertec.check.exceptions.BadRequestException;
import main.java.ru.clevertec.check.exceptions.IntertalServerException;
import main.java.ru.clevertec.check.utils.ErrorHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for managing application parameters.
 */
public class Parameters {

    @Parameter(name = "discountCard=", view = "\\d{4}", required = false)
    protected Integer discountCardNumber = 0;
    @Parameter(name = "balanceDebitCard=")
    protected Double balanceDebitCard;
    @Parameter(view = "^\\d{1,8}-\\d{1,8}$")
    protected Map<Integer, Integer> selectedProducts = new HashMap<>();
    @Parameter(name = "pathToFile=")
    protected String pathToFile;
    @Parameter(name = "saveToFile=")
    protected String saveToFile;

    /**
     * Initializes parameters from command line arguments.
     *
     * @param args the command line arguments
     * @throws BadRequestException if there is a problem with the request
     * @throws IntertalServerException if there is an internal server error
     * @throws IllegalAccessException if access to a field is illegal
     */
    public void init(String[] args) throws BadRequestException, IntertalServerException, IllegalAccessException {
            CommandLineParser cl = new CommandLineParser(args, this);
            cl.parse();
    }

}