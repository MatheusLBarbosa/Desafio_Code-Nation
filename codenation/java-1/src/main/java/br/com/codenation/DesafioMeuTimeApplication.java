package br.com.codenation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import br.com.codenation.desafio.annotation.Desafio;
import br.com.codenation.desafio.app.MeuTimeInterface;


import br.com.codenation.desafio.exceptions.CapitaoNaoInformadoException;
import br.com.codenation.desafio.exceptions.IdentificadorUtilizadoException;
import br.com.codenation.desafio.exceptions.JogadorNaoEncontradoException;
import br.com.codenation.desafio.exceptions.TimeNaoEncontradoException;

public class DesafioMeuTimeApplication implements MeuTimeInterface {

    public TreeMap<Long, Time> times = new TreeMap<Long, Time>();
    public TreeMap<Long, Jogador> jogadores = new TreeMap<Long, Jogador>();

    @Desafio("incluirTime")
    public void incluirTime(Long id, String nome, LocalDate dataCriacao, String corUniformePrincipal, String corUniformeSecundario) {
        Time time = new Time(id, nome, dataCriacao, corUniformePrincipal, corUniformeSecundario);

        if (times.containsKey(time.getId()))
            throw new IdentificadorUtilizadoException();
        else
            times.put(time.getId(), time);
    }

    @Desafio("incluirJogador")
    public void incluirJogador(Long id, Long idTime, String nome, LocalDate dataNascimento, Integer nivelHabilidade, BigDecimal salario) {
        Jogador jogador = new Jogador(id, idTime, nome, dataNascimento, nivelHabilidade, salario);

        if (!(times.containsKey(jogador.getIdTime())))
            throw new TimeNaoEncontradoException();
        else if (jogadores.containsKey(jogador.getId()))
            throw new IdentificadorUtilizadoException();

        jogadores.put(jogador.getId(), jogador);
    }

    @Desafio("definirCapitao")
    public void definirCapitao(Long idJogador) {
        if (!(jogadores.containsKey(idJogador))) throw new JogadorNaoEncontradoException();

        Long idTime = jogadores.get(idJogador).getIdTime();
        times.get(idTime).setCapitaoTime(idJogador);
    }

    @Desafio("buscarCapitaoDoTime")
    public Long buscarCapitaoDoTime(Long idTime) {
        if (!(times.containsKey(idTime))) throw new TimeNaoEncontradoException();
        if (times.get(idTime).getCapitaoTime() == null) throw new CapitaoNaoInformadoException();
        return times.get(idTime).getCapitaoTime();
    }

    @Desafio("buscarNomeJogador")
    public String buscarNomeJogador(Long idJogador) {
        if (!(jogadores.containsKey(idJogador))) throw new JogadorNaoEncontradoException();
        return jogadores.get(idJogador).getNome();
    }

    @Desafio("buscarNomeTime")
    public String buscarNomeTime(Long idTime) {
        if (!(times.containsKey(idTime))) throw new TimeNaoEncontradoException();
        return times.get(idTime).getNome();
    }

    @Desafio("buscarJogadoresDoTime")
    public List<Long> buscarJogadoresDoTime(Long idTime) {

        if (!(times.containsKey(idTime)))
            throw new TimeNaoEncontradoException();

        List<Long> identificadorJogadoresTime = new ArrayList<Long>();

        for (Map.Entry<Long, Jogador> jogador : jogadores.entrySet()) {
            if (jogador.getValue().getIdTime().equals(idTime))
                identificadorJogadoresTime.add(jogador.getKey());
        }
        return identificadorJogadoresTime;
    }

    @Desafio("buscarMelhorJogadorDoTime")
    public Long buscarMelhorJogadorDoTime(Long idTime) {
        if (!(times.containsKey(idTime))) throw new TimeNaoEncontradoException();

        Long id = null;
        Integer nivelHabilidade = -1;
        for (Map.Entry<Long, Jogador> jogador : jogadores.entrySet()) {
            if (jogador.getValue().getIdTime().equals(idTime) && jogador.getValue().getNivelHabilidade() > nivelHabilidade ) {
                id = jogador.getValue().getId();
                nivelHabilidade = jogador.getValue().getNivelHabilidade();
            }
        }
        return id;
    }

    @Desafio("buscarJogadorMaisVelho")
    public Long buscarJogadorMaisVelho(Long idTime) {
        if (!(times.containsKey(idTime))) throw new TimeNaoEncontradoException();

        Long id = null;
        LocalDate dataNasc = LocalDate.now();

        for (Map.Entry<Long, Jogador> jogador : jogadores.entrySet()) {
            if (jogador.getValue().getIdTime().equals(idTime)) {

                if ((dataNasc).isAfter(jogador.getValue().getDataNascimento())) {
                    id = jogador.getValue().getId();
                    dataNasc = jogador.getValue().getDataNascimento();
                } else if ((dataNasc).isEqual(jogador.getValue().getDataNascimento())) {
                    dataNasc = jogador.getKey() > id ? dataNasc : jogador.getValue().getDataNascimento();
                    id = jogador.getKey() > id ? id : jogador.getValue().getId();
                }
            }
        }
        return id;
    }

    @Desafio("buscarTimes")
    public List<Long> buscarTimes() {
        List<Long> identificadorTimes = new ArrayList<Long>(times.keySet());
        return identificadorTimes;
    }

    @Desafio("buscarJogadorMaiorSalario")
    public Long buscarJogadorMaiorSalario(Long idTime) {
        if (!(times.containsKey(idTime))) throw new TimeNaoEncontradoException();

        Long id = null;
        BigDecimal salario =BigDecimal.valueOf(0);
        for (Map.Entry<Long, Jogador> jogador : jogadores.entrySet()) {
            if (jogador.getValue().getIdTime().equals(idTime) && salario.compareTo(jogador.getValue().getSalario()) < 0) {
                id = jogador.getValue().getId();
                salario = jogador.getValue().getSalario();
            }
        }
        return id;
    }

    @Desafio("buscarSalarioDoJogador")
    public BigDecimal buscarSalarioDoJogador(Long idJogador) {
        if (!(jogadores.containsKey(idJogador)))
            throw new JogadorNaoEncontradoException();

        return jogadores.get(idJogador).getSalario();
    }

    @Desafio("buscarTopJogadores")
    public List<Long> buscarTopJogadores(Integer top) {

        Comparator<Jogador> compare = new Comparator<Jogador>() {
            @Override
            public int compare(Jogador j1, Jogador j2) {
                if(j1.getNivelHabilidade() != j2.getNivelHabilidade()) {
                    return j2.getNivelHabilidade()-j1.getNivelHabilidade();
                } else {
                    return (int) (j1.getId()-j2.getId());
                }
            }
        };
        Stream<Jogador> jogadoresOrdenados = jogadores.values().stream()
                .sorted(compare).limit(top);

        return jogadoresOrdenados.map(Jogador::getId).collect(Collectors.toList());
    }

    @Desafio("buscarCorCamisaTimeDeFora")
    public String buscarCorCamisaTimeDeFora(Long timeDaCasa, Long timeDeFora) {
        if (!(times.containsKey(timeDaCasa)) || !(times.containsKey(timeDeFora))) throw new TimeNaoEncontradoException();
        return (times.get(timeDaCasa).getCorUniformePrincipal().equals(times.get(timeDeFora).getCorUniformePrincipal())) ? times.get(timeDeFora).getCorUniformeSecundario() : times.get(timeDeFora).getCorUniformePrincipal();
    }

}
