package com.church.fooddonation.config;

import com.church.fooddonation.entity.*;
import com.church.fooddonation.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PreDefinicaoCestaRepository preDefinicaoRepository;

    @Autowired
    private ItemPreDefinicaoRepository itemPreDefinicaoRepository;

    @Autowired
    private DoacaoRepository doacaoRepository;

    @Autowired
    private AlimentoEstoqueRepository alimentoEstoqueRepository;

    @Autowired
    private BeneficiadoRepository beneficiadoRepository;

    @Override
    public void run(String... args) throws Exception {
        // Criar usuário admin padrão
        criarUsuarioAdmin();
        
        // Criar dados de exemplo se não existirem
        criarDadosExemplo();
    }

    private void criarUsuarioAdmin() {
        if (!usuarioRepository.existsByUsername("admin")) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@sistema.com");
            admin.setRole("ADMIN");
            
            usuarioRepository.save(admin);
            System.out.println("✅ Usuário administrador criado automaticamente:");
            System.out.println("   Usuário: admin");
            System.out.println("   Senha: admin123");
            System.out.println("   Role: ADMIN");
        } else {
            System.out.println("✅ Usuário administrador já existe no sistema");
        }
    }

    private void criarDadosExemplo() {
        // Verificar se já existem dados
        if (preDefinicaoRepository.count() > 0) {
            System.out.println("✅ Dados de exemplo já existem no sistema");
            return;
        }

        System.out.println("🔄 Criando dados de exemplo...");

        // Criar beneficiados de exemplo
        criarBeneficiadosExemplo();

        // Criar doações e estoque de exemplo
        criarDoacoesEstoqueExemplo();

        // Criar pré-definições de cestas
        criarPreDefinicoesCestasExemplo();

        System.out.println("✅ Dados de exemplo criados com sucesso!");
    }

    private void criarBeneficiadosExemplo() {
        String[] nomes = {"Maria Silva", "João Santos", "Ana Costa", "Pedro Oliveira", "Lucia Ferreira"};
        String[] cpfs = {"123.456.789-01", "234.567.890-12", "345.678.901-23", "456.789.012-34", "567.890.123-45"};
        String[] telefones = {"(11) 99999-1111", "(11) 99999-2222", "(11) 99999-3333", "(11) 99999-4444", "(11) 99999-5555"};

        for (int i = 0; i < nomes.length; i++) {
            Beneficiado beneficiado = new Beneficiado();
            beneficiado.setNome(nomes[i]);
            beneficiado.setCpf(cpfs[i]);
            beneficiado.setTelefone(telefones[i]);
            beneficiado.setEndereco("Rua Exemplo, " + (100 + i) + " - São Paulo, SP");
            beneficiado.setDataNascimento(LocalDate.now().minusYears(30 + i));
            beneficiado.setNumeroDependentes(i % 3);
            beneficiado.setRendaFamiliar(new BigDecimal("1000.00"));
            beneficiadoRepository.save(beneficiado);
        }
    }

    private void criarDoacoesEstoqueExemplo() {
        // Criar doações de exemplo
        String[] alimentos = {"Arroz", "Feijão", "Óleo", "Açúcar", "Macarrão", "Farinha de Trigo", "Leite em Pó", "Café"};
        BigDecimal[] quantidades = {
            new BigDecimal("50"), new BigDecimal("30"), new BigDecimal("20"), 
            new BigDecimal("25"), new BigDecimal("40"), new BigDecimal("15"), 
            new BigDecimal("8"), new BigDecimal("12")
        };
        String[] unidades = {"kg", "kg", "litro", "kg", "kg", "kg", "kg", "kg"};
        int[] diasValidade = {180, 365, 730, 365, 545, 365, 180, 365};

        for (int i = 0; i < alimentos.length; i++) {
            // Criar doação
            Doacao doacao = new Doacao();
            doacao.setTipoAlimento(alimentos[i]);
            doacao.setQuantidade(quantidades[i]);
            doacao.setUnidadeMedida(unidades[i]);
            doacao.setDataRecebimento(LocalDate.now().minusDays(5));
            doacao.setDataValidade(LocalDate.now().plusDays(diasValidade[i]));
            doacao.setOrigem("Supermercado Central");
            doacao.setObservacoes("Doação mensal de alimentos básicos");
            doacao = doacaoRepository.save(doacao);

            // Criar item de estoque correspondente
            AlimentoEstoque estoque = new AlimentoEstoque();
            estoque.setDoacao(doacao);
            estoque.setTipoAlimento(alimentos[i]);
            estoque.setQuantidadeDisponivel(quantidades[i]);
            estoque.setDataValidade(LocalDate.now().plusDays(diasValidade[i]));
            estoque.setLocalArmazenamento("Depósito Principal");
            alimentoEstoqueRepository.save(estoque);
        }

        // Criar alguns itens próximos ao vencimento para demonstrar alertas
        String[] alimentosVencendo = {"Pão", "Frutas", "Verduras"};
        int[] diasVencimento = {3, 7, 15};
        BigDecimal[] qtdVencendo = {new BigDecimal("5"), new BigDecimal("8"), new BigDecimal("12")};
        String[] unidadesVencendo = {"unidade", "kg", "kg"};

        for (int i = 0; i < alimentosVencendo.length; i++) {
            // Criar doação
            Doacao doacao = new Doacao();
            doacao.setTipoAlimento(alimentosVencendo[i]);
            doacao.setQuantidade(qtdVencendo[i]);
            doacao.setUnidadeMedida(unidadesVencendo[i]);
            doacao.setDataRecebimento(LocalDate.now().minusDays(2));
            doacao.setDataValidade(LocalDate.now().plusDays(diasVencimento[i]));
            doacao.setOrigem("Padaria Local");
            doacao.setObservacoes("Doação de produtos próximos ao vencimento");
            doacao = doacaoRepository.save(doacao);

            // Criar item de estoque correspondente
            AlimentoEstoque estoque = new AlimentoEstoque();
            estoque.setDoacao(doacao);
            estoque.setTipoAlimento(alimentosVencendo[i]);
            estoque.setQuantidadeDisponivel(qtdVencendo[i]);
            estoque.setDataValidade(LocalDate.now().plusDays(diasVencimento[i]));
            estoque.setLocalArmazenamento("Depósito Principal");
            alimentoEstoqueRepository.save(estoque);
        }
    }

    private void criarPreDefinicoesCestasExemplo() {
        // Cesta Básica Familiar
        PreDefinicaoCesta cestaFamiliar = new PreDefinicaoCesta();
        cestaFamiliar.setNome("Cesta Básica Familiar");
        cestaFamiliar.setDescricao("Cesta básica para família de 4 pessoas (1 mês)");
        cestaFamiliar.setAtiva(true);
        cestaFamiliar = preDefinicaoRepository.save(cestaFamiliar);

        // Itens da cesta familiar
        String[][] itensFamiliar = {
            {"Arroz", "5", "kg"},
            {"Feijão", "2", "kg"},
            {"Óleo", "1", "litro"},
            {"Açúcar", "2", "kg"},
            {"Macarrão", "2", "kg"},
            {"Farinha de Trigo", "1", "kg"},
            {"Café", "500", "g"}
        };

        for (String[] item : itensFamiliar) {
            ItemPreDefinicao itemPre = new ItemPreDefinicao();
            itemPre.setPreDefinicaoCesta(cestaFamiliar);
            itemPre.setTipoAlimento(item[0]);
            itemPre.setQuantidade(new BigDecimal(item[1]));
            itemPre.setUnidadeMedida(item[2]);
            itemPre.setObservacoes("Item padrão da cesta familiar");
            itemPreDefinicaoRepository.save(itemPre);
        }

        // Cesta Básica Individual
        PreDefinicaoCesta cestaIndividual = new PreDefinicaoCesta();
        cestaIndividual.setNome("Cesta Básica Individual");
        cestaIndividual.setDescricao("Cesta básica para 1 pessoa (1 mês)");
        cestaIndividual.setAtiva(true);
        cestaIndividual = preDefinicaoRepository.save(cestaIndividual);

        // Itens da cesta individual
        String[][] itensIndividual = {
            {"Arroz", "2", "kg"},
            {"Feijão", "1", "kg"},
            {"Óleo", "500", "ml"},
            {"Açúcar", "1", "kg"},
            {"Macarrão", "1", "kg"},
            {"Café", "250", "g"}
        };

        for (String[] item : itensIndividual) {
            ItemPreDefinicao itemPre = new ItemPreDefinicao();
            itemPre.setPreDefinicaoCesta(cestaIndividual);
            itemPre.setTipoAlimento(item[0]);
            itemPre.setQuantidade(new BigDecimal(item[1]));
            itemPre.setUnidadeMedida(item[2]);
            itemPre.setObservacoes("Item padrão da cesta individual");
            itemPreDefinicaoRepository.save(itemPre);
        }

        // Cesta de Emergência
        PreDefinicaoCesta cestaEmergencia = new PreDefinicaoCesta();
        cestaEmergencia.setNome("Cesta de Emergência");
        cestaEmergencia.setDescricao("Cesta básica para situações emergenciais (1 semana)");
        cestaEmergencia.setAtiva(true);
        cestaEmergencia = preDefinicaoRepository.save(cestaEmergencia);

        // Itens da cesta de emergência
        String[][] itensEmergencia = {
            {"Arroz", "1", "kg"},
            {"Feijão", "500", "g"},
            {"Óleo", "250", "ml"},
            {"Açúcar", "500", "g"},
            {"Macarrão", "500", "g"}
        };

        for (String[] item : itensEmergencia) {
            ItemPreDefinicao itemPre = new ItemPreDefinicao();
            itemPre.setPreDefinicaoCesta(cestaEmergencia);
            itemPre.setTipoAlimento(item[0]);
            itemPre.setQuantidade(new BigDecimal(item[1]));
            itemPre.setUnidadeMedida(item[2]);
            itemPre.setObservacoes("Item para situação emergencial");
            itemPreDefinicaoRepository.save(itemPre);
        }
    }
}

