package com.udea.lab12026p;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class Lab12026pApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private long crearCliente(String cuenta, double saldo) throws Exception {
        String body = """
                {"firstName":"Ana","lastName":"Perez","accountNumber":"%s","balance":%s}
                """.formatted(cuenta, saldo);
        String respuesta = mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(respuesta).get("id").asLong();
    }

    private String transferencia(String emisor, String receptor, double monto) {
        return """
                {"senderAccountNumber":"%s","receiverAccountNumber":"%s","amount":%s}
                """.formatted(emisor, receptor, monto);
    }

    @Test
    void contextLoads() {
    }

    @Test
    void listarClientes() throws Exception {
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk());
    }

    @Test
    void crearYBuscarCliente() throws Exception {
        long id = crearCliente("1001", 1000.0);
        mockMvc.perform(get("/api/customers/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Ana"))
                .andExpect(jsonPath("$.accountNumber").value("1001"));
    }

    @Test
    void actualizarCliente() throws Exception {
        long id = crearCliente("1002", 500.0);
        String body = """
                {"firstName":"Maria","lastName":"Gomez","balance":750.0}
                """;
        mockMvc.perform(put("/api/customers/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Maria"))
                .andExpect(jsonPath("$.balance").value(750.0));
    }

    @Test
    void eliminarCliente() throws Exception {
        long id = crearCliente("1003", 100.0);
        mockMvc.perform(delete("/api/customers/" + id))
                .andExpect(status().isNoContent());
        assertThrows(Exception.class, () -> mockMvc.perform(get("/api/customers/" + id)));
    }

    @Test
    void buscarClienteInexistente() {
        assertThrows(Exception.class, () -> mockMvc.perform(get("/api/customers/999999")));
    }

    @Test
    void crearClienteSinSaldo() {
        String body = """
                {"firstName":"Sin","lastName":"Saldo","accountNumber":"1004"}
                """;
        assertThrows(Exception.class, () -> mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)));
    }

    @Test
    void transferenciaExitosa() throws Exception {
        long idEmisor = crearCliente("2001", 1000.0);
        long idReceptor = crearCliente("2002", 500.0);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transferencia("2001", "2002", 200.0)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(200.0));

        mockMvc.perform(get("/api/customers/" + idEmisor))
                .andExpect(jsonPath("$.balance").value(800.0));
        mockMvc.perform(get("/api/customers/" + idReceptor))
                .andExpect(jsonPath("$.balance").value(700.0));
    }

    @Test
    void transferenciaConSaldoInsuficiente() throws Exception {
        crearCliente("3001", 50.0);
        crearCliente("3002", 0.0);
        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transferencia("3001", "3002", 500.0)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void transferenciaConCuentaInexistente() throws Exception {
        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transferencia("9999991", "9999992", 10.0)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void transferenciaSinNumerosDeCuenta() throws Exception {
        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":10.0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void historialDeUnaCuenta() throws Exception {
        crearCliente("4001", 1000.0);
        crearCliente("4002", 0.0);
        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transferencia("4001", "4002", 150.0)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/transactions/4001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].amount").value(150.0));
    }

    @Test
    void historialDeCuentaSinMovimientos() throws Exception {
        mockMvc.perform(get("/api/transactions/0000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
	@Test
    void healthCheck() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string("Banco digital OK"));
    }
}