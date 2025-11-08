package com.proyecto.cabapro.service;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;

import com.proyecto.cabapro.enums.EstadoPartido;
import com.proyecto.cabapro.model.Partido;
import com.proyecto.cabapro.model.Torneo;
import com.proyecto.cabapro.repository.PartidoRepository;

class PartidoServiceTest {

    @Mock
    private PartidoRepository partidoRepository;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private PartidoService partidoService;

    private Torneo torneo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        torneo = new Torneo();
        torneo.setIdTorneo(1);
        torneo.setFechaInicio(LocalDateTime.of(2025, 10, 1, 0, 0));
        torneo.setFechaFin(LocalDateTime.of(2025, 12, 31, 0, 0));

        // Mensajes de error simulados
        when(messageSource.getMessage(anyString(), any(), any()))
            .thenReturn("Error de validación");
    }

    @Test
    void guardarPartidoValido_deberiaGuardar() {
        Partido partido = new Partido();
        partido.setFecha(LocalDateTime.of(2025, 11, 10, 10, 0));
        partido.setLugar("Coliseo EAFIT");
        partido.setTorneo(torneo);
        partido.setEstadoPartido(EstadoPartido.PROGRAMADO);

        when(partidoRepository.save(any(Partido.class))).thenReturn(partido);

        Partido resultado = partidoService.savePartido(partido);

        assertNotNull(resultado);
        verify(partidoRepository, times(1)).save(partido);
    }

    @Test
    void guardarPartido_sinTorneo_deberiaLanzarExcepcion() {
        Partido partido = new Partido();
        partido.setFecha(LocalDateTime.of(2025, 11, 10, 10, 0));
        partido.setLugar("Coliseo EAFIT");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            partidoService.savePartido(partido);
        });

        assertEquals("Error de validación", ex.getMessage());
        verify(partidoRepository, never()).save(any());
    }

    @Test
    void guardarPartido_fueraDeRango_deberiaLanzarExcepcion() {
        Partido partido = new Partido();
        partido.setFecha(LocalDateTime.of(2026, 1, 5, 10, 0)); // fuera del rango
        partido.setLugar("Coliseo EAFIT");
        partido.setTorneo(torneo);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            partidoService.savePartido(partido);
        });

        assertEquals("Error de validación", ex.getMessage());
        verify(partidoRepository, never()).save(any());
    }
}
