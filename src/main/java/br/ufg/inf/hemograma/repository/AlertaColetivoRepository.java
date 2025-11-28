package br.ufg.inf.hemograma.repository;

import br.ufg.inf.hemograma.model.AlertaColetivo;
import br.ufg.inf.hemograma.model.enums.TipoParametro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlertaColetivoRepository extends JpaRepository<AlertaColetivo, Long> {
    
    @Query("SELECT a FROM AlertaColetivo a WHERE a.janelaFim >= :dataInicio ORDER BY a.dataCriacao DESC")
    List<AlertaColetivo> findRecentAlertas(@Param("dataInicio") LocalDateTime dataInicio);
    
    @Query("SELECT a FROM AlertaColetivo a WHERE a.notificacaoEnviada = false")
    List<AlertaColetivo> findAlertasPendentesNotificacao();
}

