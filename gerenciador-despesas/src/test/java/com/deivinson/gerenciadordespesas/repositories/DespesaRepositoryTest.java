package com.deivinson.gerenciadordespesas.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.deivinson.gerenciadordespesas.entities.Categoria;
import com.deivinson.gerenciadordespesas.entities.Despesa;
import com.deivinson.gerenciadordespesas.tests.Factory;

@DataJpaTest
@AutoConfigureTestDatabase
public class DespesaRepositoryTest {
	
	@Autowired
	private DespesaRepository despesaRepository;

	private Long existingId;
	private Long nonExistingId;
	private Long countTotalDespesas;
	
	@BeforeEach
	void setUp() {
		
		existingId = 1L;
		nonExistingId = 999L;
		countTotalDespesas = 13L;
	}
	
	@Test
	public void testSaveDespesa() {
		
		Despesa despesa = Factory.construtorDespesaVazio();
		despesa.setId(existingId);
		despesa.setValor(100.00);
		
		despesaRepository.save(despesa);
		
		Despesa despesaSalva = despesaRepository.findById(despesa.getId()).orElse(null);
		
		assertNotNull(despesaSalva);
		assertEquals(existingId, despesaSalva.getId());
		assertEquals(despesa, despesaSalva);
		assertEquals(100.00, despesaSalva.getValor());
	}
	
	@Test
	public void testFindDespesaById() {
		
		Despesa despesa = Factory.construtorDespesaVazio();
		despesa.setValor(100.00);
		despesaRepository.save(despesa);
		
		Long despesaId = despesa.getId();
		Despesa despesaEncontrada = despesaRepository.findById(despesaId).orElse(null);
		
		assertNotNull(despesaEncontrada);
		assertEquals(despesaId, despesaEncontrada.getId());
		assertEquals(100.00, despesaEncontrada.getValor());
	}

	@Test
	public void testFindDespesaByIdNotFound() {
		
		Despesa despesaEncontrada = despesaRepository.findById(nonExistingId).orElse(null);
		
		assertNull(despesaEncontrada);
	}
	
	@Test
	public void testFindAllDespesa() {
		
		Despesa despesa1 = Factory.construtorDespesaVazio();
        despesa1.setValor(100.00);

        Despesa despesa2 = Factory.construtorDespesaVazio();
        despesa2.setValor(200.00);
        
        Despesa despesa3 = Factory.construtorDespesaVazio();
        despesa3.setValor(300.00);
        
        despesaRepository.save(despesa1);
        despesaRepository.save(despesa2);
        despesaRepository.save(despesa3);
        
        List<Despesa> todasAsDespesas = despesaRepository.findAll();
        
        assertFalse(todasAsDespesas.isEmpty());
        assertEquals(countTotalDespesas + 3, todasAsDespesas.size());
        
        assertTrue(todasAsDespesas.stream().anyMatch(c -> c.getValor().equals(100.00)));
        assertTrue(todasAsDespesas.stream().anyMatch(c -> c.getValor().equals(200.00)));
        assertTrue(todasAsDespesas.stream().anyMatch(c -> c.getValor().equals(300.00)));
        
	}
	
	@Test
	public void testUpdateDespesa(){
		
		Despesa despesa = despesaRepository.findById(1L).orElse(null);
		
		despesa.setId(1L);
		despesa.setValor(100.00);
		
		despesaRepository.save(despesa);
		
		assertEquals(1L, despesa.getId());
		assertEquals(100.00, despesa.getValor());
		
		despesa.setId(35L);
		despesa.setValor(300.00);
		
		despesaRepository.save(despesa);
		assertNotEquals(1L, despesa.getId());
		assertFalse(despesa.getValor() == 100.00);
		assertEquals(35L, despesa.getId());
		assertTrue(despesa.getValor() == 300.00);
		
	}
	
	@Test
	public void deleteDespesa() {
		
		Despesa despesa = Factory.construtorDespesaComArgumentos();
		
		assertEquals(1L, despesa.getId());
		assertTrue(despesa.getValor() == 100.00);
		
		despesaRepository.deleteById(1L);
		
		Optional<Despesa> result = despesaRepository.findById(existingId);
		
		assertFalse(result.isPresent());
	}
	
	@Test
	public void saveShouldPersistWithAutoincrementWhenIdIsNull() {

		Despesa despesa = Factory.construtorDespesaVazio();
		despesa.setId(null);
		
		despesa = despesaRepository.save(despesa);
		
		assertNotNull(despesa);
		assertEquals(countTotalDespesas + 2L, despesa.getId());
	}
	
	@Test
	public void OneToManyRelationshipDespesaForDespesa () {
		
		Despesa despesa = Factory.construtorDespesaComArgumentos();
		
		despesaRepository.save(despesa);
		
		Despesa despesaRelacao = despesaRepository.findById(despesa.getCategoria().getId()).orElse(null); 
		assertNotNull(despesaRelacao);
		assertEquals(1L, despesaRelacao.getCategoria().getId());
		assertEquals(1L, despesaRelacao.getUsuario().getId());
	}
	
	@Test
    public void testCalculateTotalDespesa() {
		
        Double total = despesaRepository.calcularDespesaTotal();
        
        assertEquals(8689.36, total);
	}
	
	@Test
    public void testCalculateTotalDespesaByCategoria() {
		
        Categoria categoria = Factory.construtorCategoriaComArgumentosEDespesa();
        
        Double total = despesaRepository.calcularDespesaTotalPorCategoria(categoria);
        
        assertEquals(439.36, total);
    }
	
	@Test
    public void testCalculateTotalDespesaByCategoriaAndData() {
		
        Categoria categoria = Factory.construtorCategoriaComArgumentosEDespesa();
        
        LocalDate dataInicio = LocalDate.of(2023, 7, 1);
        LocalDate dataFim = LocalDate.of(2023, 8, 31);
        
        Double total = despesaRepository.calcularValorTotalDespesasPorCategoriaEData(categoria, dataInicio, dataFim);

        assertEquals(439.36, total);
    }
	
	@Test
    public void testCalculateTotalDespesaByPeriod() {
		
        LocalDate dataInicio = LocalDate.of(2023, 7, 1);
        LocalDate dataFim = LocalDate.of(2023, 8, 31);
        Double total = despesaRepository.calcularSomaTotalDespesasPorPeriodo(dataInicio, dataFim);
        
        assertEquals(5939.36, total);
    }
	
}
