package com.udea.lab12026p.controller;

import com.udea.lab12026p.dto.TransactionDTO;
import com.udea.lab12026p.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.http.ResponseEntity; // Permite construir las respuestas HTTP que devuelve la API.
import org.springframework.web.bind.annotation.*; // Importa las anotaciones de Spring para crear endpoints,

import java.util.List; // Permite utilizar listas de objetos, en este caso una lista de CustomerDTO.


@RestController // Indica que esta clase es un controlador REST.
// Spring la reconoce como una clase que recibe solicitudes HTTP
@RequestMapping(value="/api/transactions", produces = "application/json") // Define la ruta base para todos los endpoints de este controlador.
public class TransactionController {

    @Autowired
    private TransactionService transactionService;


    @PostMapping
    public ResponseEntity<?> transferMoney(@RequestBody TransactionDTO transactionDTO) {
        try {
            TransactionDTO savedTransaction = transactionService.transferMoney(transactionDTO);
            return ResponseEntity.ok(savedTransaction);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{accountNumber}")
    public List<TransactionDTO> getTransactionsByAccount(@PathVariable String accountNumber) {
        return transactionService.getTransactionsForAccount(accountNumber);
    }
}