package main.java.ru.clevertec.check.controller;

import main.java.ru.clevertec.check.cli.parser.*;
import main.java.ru.clevertec.check.exceptions.*;
import main.java.ru.clevertec.check.model.*;
import main.java.ru.clevertec.check.services.impl.*;
import main.java.ru.clevertec.check.view.Receipt;
import main.java.ru.clevertec.check.view.ReceiptSaver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AppController {


    private ProductService productService;
    private DiscountCardService discountCardService;
    private final String PATH_TO_PRODUCTS_FILE = "./src/main/resources/products.csv";
    private final String PATH_TO_CARDS_FILE = "./src/main/resources/discountCards.csv";

    Parameters parameters;

    class Parameters {
        @Parameter(name = "discountCard=", view = "\\d{4}", required = false)
        Integer discountCardNumber = 0;

        @Parameter(name = "balanceDebitCard=")
        Double balanceDebitCard;

        @Parameter(view = "^\\d{1,8}-\\d{1,8}$")
        Map<Integer,Integer> selectedProducts = new HashMap<>() ;
    }

    public void start(String[] args) throws NotEnoughMoneyException, BadRequestException, IOException {
        processCommandLineArgs(args);
        processServices();
        processOrder();
    }
    public void processCommandLineArgs(String[] args)  {
        try {
            parameters = new Parameters();
            CommandLineParser pCL = new CommandLineParser(args, parameters);
            pCL.parseAll();
            System.out.println(parameters.toString());
        } catch (BadRequestException e){
            System.out.println("bad req");
        } catch (IntertalServerException | IllegalAccessException e){
            System.out.println("server error");
        }

    }

    private void  processServices ()  {
        try {
            productService = new ProductService(PATH_TO_PRODUCTS_FILE);
            productService.load();

            discountCardService = new DiscountCardService(PATH_TO_CARDS_FILE);
            discountCardService.load();
        } catch (IOException e){
            System.out.println("server error (io)");
        }

    }

    private void processOrder() throws BadRequestException, NotEnoughMoneyException, IOException {
        OrderController controller = new OrderController(parameters);

        Optional<DiscountCard> discountCardO = discountCardService.getDiscountByNumber(parameters.discountCardNumber);

        Order order;
        Receipt receipt;

        if (discountCardO.isPresent()) {
            order = controller.createOrder(productService, discountCardO.get().getDiscountPercentage());
            receipt = new Receipt(order, discountCardO.get(), parameters.balanceDebitCard);
        } else {
            order = controller.createOrder(productService, 0);
            receipt = new Receipt(order, parameters.balanceDebitCard);
        }

        System.out.println(receipt.toString());

        ReceiptSaver printer = new ReceiptSaver();
        printer.generateCheck(receipt,"./Res.csv");

        if (order.getTotalWithDiscount() > parameters.balanceDebitCard) {
            throw new NotEnoughMoneyException("No money!");
        }

    }
}
