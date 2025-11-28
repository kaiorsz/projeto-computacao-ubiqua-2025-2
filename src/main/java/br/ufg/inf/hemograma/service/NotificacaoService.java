package br.ufg.inf.hemograma.service;

import br.ufg.inf.hemograma.model.AlertaColetivo;
import br.ufg.inf.hemograma.model.Desvio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificacaoService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificacaoService.class);
    
    public void enviarNotificacaoDesvioIndividual(Desvio desvio) {
        logger.warn("📧 NOTIFICACAO: Desvio individual detectado - {} (Severidade: {})", 
                   desvio.getTipoParametro().getNome(), 
                   desvio.getSeveridade().getDescricao());
    }
    
    public void enviarNotificacaoAlertaColetivo(AlertaColetivo alerta) {
        logger.error("🚨 NOTIFICACAO CRITICA: Alerta coletivo - {} ({} desvios, {}% afetados)", 
                    alerta.getTipoParametro().getNome(),
                    alerta.getTotalDesvios(),
                    String.format("%.1f", alerta.getPercentualAfetados()));
    }
}

