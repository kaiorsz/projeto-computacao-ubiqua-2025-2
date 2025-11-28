package br.ufg.inf.hemograma.service;

import br.ufg.inf.hemograma.model.AlertaColetivo;
import br.ufg.inf.hemograma.model.Desvio;
import br.ufg.inf.hemograma.model.enums.TipoParametro;
import br.ufg.inf.hemograma.repository.AlertaColetivoRepository;
import br.ufg.inf.hemograma.repository.DesvioRepository;
import br.ufg.inf.hemograma.repository.HemogramaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnaliseColetivaService {
    
    private static final Logger logger = LoggerFactory.getLogger(AnaliseColetivaService.class);
    
    @Autowired
    private DesvioRepository desvioRepository;
    
    @Autowired
    private HemogramaRepository hemogramaRepository;
    
    @Autowired
    private AlertaColetivoRepository alertaColetivoRepository;
    
    @Autowired
    private NotificacaoService notificacaoService;
    
    @Value("${app.hemograma.analise-coletiva.janela-deslizante-horas:24}")
    private int janelaHoras;
    
    @Value("${app.hemograma.analise-coletiva.limite-alertas-criticos:5}")
    private int limiteAlertasCriticos;
    
    @Value("${app.hemograma.analise-coletiva.percentual-desvio-critico:50.0}")
    private double percentualDesviosCritico;
    
    @Scheduled(fixedRate = 300000)
    @Transactional
    public void analisarJanelaDeslizante() {
        LocalDateTime janelaFim = LocalDateTime.now();
        LocalDateTime janelaInicio = janelaFim.minusHours(janelaHoras);
        
        logger.info("Analisando janela deslizante: {} a {}", janelaInicio, janelaFim);
        
        long totalHemogramas = hemogramaRepository.countByDataCadastroBetween(janelaInicio, janelaFim);
        
        if (totalHemogramas == 0) {
            return;
        }
        
        for (TipoParametro tipo : TipoParametro.values()) {
            analisarParametroNaJanela(tipo, janelaInicio, janelaFim, totalHemogramas);
        }
    }
    
    private void analisarParametroNaJanela(TipoParametro tipo, LocalDateTime inicio, LocalDateTime fim, long totalHemogramas) {
        List<Desvio> desvios = desvioRepository.findByTipoParametroAndDataCriacaoBetween(tipo, inicio, fim);
        
        if (desvios.size() >= limiteAlertasCriticos) {
            double percentualAfetados = (desvios.size() * 100.0) / totalHemogramas;
            
            if (percentualAfetados >= percentualDesviosCritico) {
                criarAlertaColetivo(tipo, desvios.size(), inicio, fim, percentualAfetados);
            }
        }
    }
    
    private void criarAlertaColetivo(TipoParametro tipo, int totalDesvios, LocalDateTime inicio, LocalDateTime fim, double percentual) {
        AlertaColetivo alerta = new AlertaColetivo();
        alerta.setTipoParametro(tipo);
        alerta.setTotalDesvios(totalDesvios);
        alerta.setJanelaInicio(inicio);
        alerta.setJanelaFim(fim);
        alerta.setPercentualAfetados(percentual);
        alerta.setDescricao(String.format(
            "ALERTA COLETIVO: %d desvios de %s detectados (%.1f%% dos hemogramas) nas ultimas %d horas",
            totalDesvios, tipo.getNome(), percentual, janelaHoras
        ));
        
        alertaColetivoRepository.save(alerta);
        logger.error("🚨 {}", alerta.getDescricao());
        
        notificacaoService.enviarNotificacaoAlertaColetivo(alerta);
    }
}

