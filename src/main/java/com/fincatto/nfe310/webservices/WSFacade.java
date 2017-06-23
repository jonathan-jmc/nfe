package com.fincatto.nfe310.webservices;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import org.apache.commons.httpclient.protocol.Protocol;

import com.fincatto.nfe310.NFeConfig;
import com.fincatto.nfe310.classes.NFModelo;
import com.fincatto.nfe310.classes.NFUnidadeFederativa;
import com.fincatto.nfe310.classes.cadastro.NFRetornoConsultaCadastro;
import com.fincatto.nfe310.classes.evento.NFEnviaEventoRetorno;
import com.fincatto.nfe310.classes.evento.distribuicaodfe.RetDistDFeInt;
import com.fincatto.nfe310.classes.evento.downloadnf.NFDownloadNFeRetorno;
import com.fincatto.nfe310.classes.evento.inutilizacao.NFRetornoEventoInutilizacao;
import com.fincatto.nfe310.classes.evento.manifestacaodestinatario.NFTipoEventoManifestacaoDestinatario;
import com.fincatto.nfe310.classes.lote.consulta.NFLoteConsultaRetorno;
import com.fincatto.nfe310.classes.lote.envio.NFLoteEnvio;
import com.fincatto.nfe310.classes.lote.envio.NFLoteEnvioRetorno;
import com.fincatto.nfe310.classes.lote.envio.NFLoteEnvioRetornoDados;
import com.fincatto.nfe310.classes.lote.envio.NFLoteIndicadorProcessamento;
import com.fincatto.nfe310.classes.nota.consulta.NFNotaConsultaRetorno;
import com.fincatto.nfe310.classes.statusservico.consulta.NFStatusServicoConsultaRetorno;

public class WSFacade {

    private final WSLoteEnvio wsLoteEnvio;
    private final WSLoteConsulta wsLoteConsulta;
    private final WSStatusConsulta wsStatusConsulta;
    private final WSNotaConsulta wsNotaConsulta;
    private final WSCartaCorrecao wsCartaCorrecao;
    private final WSCancelamento wsCancelamento;
    private final WSConsultaCadastro wsConsultaCadastro;
    private final WSInutilizacao wsInutilizacao;
    private final WSManifestacaoDestinatario wSManifestacaoDestinatario;
    private final WSNotaDownload wsNotaDownload;
    private final WSDistribuicaoDocumentoFiscal wsDistribuicaoDocumentoFiscal;

    public WSFacade(final NFeConfig config) throws IOException, KeyManagementException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException {
        Protocol.registerProtocol("https", new Protocol("https", new NFSocketFactory(config), 443));

        // inicia os servicos disponiveis da nfe
        this.wsLoteEnvio = new WSLoteEnvio(config);
        this.wsLoteConsulta = new WSLoteConsulta(config);
        this.wsStatusConsulta = new WSStatusConsulta(config);
        this.wsNotaConsulta = new WSNotaConsulta(config);
        this.wsCartaCorrecao = new WSCartaCorrecao(config);
        this.wsCancelamento = new WSCancelamento(config);
        this.wsConsultaCadastro = new WSConsultaCadastro(config);
        this.wsInutilizacao = new WSInutilizacao(config);
        this.wSManifestacaoDestinatario = new WSManifestacaoDestinatario(config);
        this.wsNotaDownload = new WSNotaDownload(config);
        this.wsDistribuicaoDocumentoFiscal = new WSDistribuicaoDocumentoFiscal(config);
        
    }

    /**
     * Faz o envio de lote para a Sefaz
     *
     * @param lote o lote a ser enviado para a Sefaz
     * @return dados do lote retornado pelo webservice, alem do lote assinado
     * @throws Exception caso nao consiga gerar o xml ou problema de conexao com o sefaz
     */
    public NFLoteEnvioRetornoDados enviaLote(final NFLoteEnvio lote) throws Exception {
        if (lote.getIndicadorProcessamento().equals(NFLoteIndicadorProcessamento.PROCESSAMENTO_SINCRONO)) {
            throw new IllegalStateException("Nao existe ainda a forma de envio sincrona, faca o envio de forma assincrona");
        }
        return this.wsLoteEnvio.enviaLote(lote);
    }

    /**
     * Faz o envio assinado para a Sefaz de NF-e e NFC-e
     * ATENCAO: Esse metodo deve ser utilizado para assinaturas A3
     *
     * @param loteAssinadoXml lote assinado no formato XML
     * @param modelo          modelo da nota (NF-e ou NFC-e)
     * @return dados do lote retornado pelo webservice
     * @throws Exception caso nao consiga gerar o xml ou problema de conexao com o sefaz
     */
    public NFLoteEnvioRetorno enviaLoteAssinado(final String loteAssinadoXml, final NFModelo modelo) throws Exception {
        return this.wsLoteEnvio.enviaLoteAssinado(loteAssinadoXml, modelo);
    }

    /**
     * Faz a consulta do lote na Sefaz (NF-e e NFC-e)
     *
     * @param numeroRecibo numero do recibo do processamento
     * @param modelo       modelo da nota (NF-e ou NFC-e)
     * @return dados de consulta de lote retornado pelo webservice
     * @throws Exception caso nao consiga gerar o xml ou problema de conexao com o sefaz
     */
    public NFLoteConsultaRetorno consultaLote(final String numeroRecibo, final NFModelo modelo) throws Exception {
        return this.wsLoteConsulta.consultaLote(numeroRecibo, modelo);
    }

    /**
     * Faz a consulta de status responsavel pela UF
     *
     * @param uf     uf UF que deseja consultar o status do sefaz responsavel
     * @param modelo modelo da nota (NF-e ou NFC-e)
     * @return dados da consulta de status retornado pelo webservice
     * @throws Exception caso nao consiga gerar o xml ou problema de conexao com o sefaz
     */
    public NFStatusServicoConsultaRetorno consultaStatus(final NFUnidadeFederativa uf, final NFModelo modelo) throws Exception {
        return this.wsStatusConsulta.consultaStatus(uf, modelo);
    }

    /**
     * Faz a consulta da nota
     *
     * @param chaveDeAcesso chave de acesso da nota
     * @return dados da consulta da nota retornado pelo webservice
     * @throws Exception caso nao consiga gerar o xml ou problema de conexao com o sefaz
     */
    public NFNotaConsultaRetorno consultaNota(final String chaveDeAcesso) throws Exception {
        return this.wsNotaConsulta.consultaNota(chaveDeAcesso);
    }

    /**
     * Faz a correcao da nota
     *
     * @param chaveDeAcesso          chave de acesso da nota
     * @param textoCorrecao          texto de correcao
     * @param numeroSequencialEvento numero sequencial de evento, esse numero nao pode ser repetido!
     * @return dados da correcao da nota retornado pelo webservice
     * @throws Exception caso nao consiga gerar o xml ou problema de conexao com o sefaz
     */
    public NFEnviaEventoRetorno corrigeNota(final String chaveDeAcesso, final String textoCorrecao, final int numeroSequencialEvento) throws Exception {
        return this.wsCartaCorrecao.corrigeNota(chaveDeAcesso, textoCorrecao, numeroSequencialEvento);
    }

    /**
     * Faz a correcao da nota com o evento ja assinado
     * ATENCAO: Esse metodo deve ser utilizado para assinaturas A3
     *
     * @param chave       chave de acesso da nota
     * @param eventoAssinadoXml evento ja assinado em formato XML
     * @return dados da correcao da nota retornado pelo webservice
     * @throws Exception caso nao consiga gerar o xml ou problema de conexao com o sefaz
     */
    public NFEnviaEventoRetorno corrigeNotaAssinada(final String chave, final String eventoAssinadoXml) throws Exception {
        return this.wsCartaCorrecao.corrigeNotaAssinada(chave, eventoAssinadoXml);
    }

    /**
     * Faz o cancelamento da nota
     *
     * @param chave     chave de acesso da nota
     * @param numeroProtocolo numero do protocolo da nota
     * @param motivo          motivo do cancelamento
     * @return dados do cancelamento da nota retornado pelo webservice
     * @throws Exception caso nao consiga gerar o xml ou problema de conexao com o sefaz
     */
    public NFEnviaEventoRetorno cancelaNota(final String chave, final String numeroProtocolo, final String motivo) throws Exception {
        return this.wsCancelamento.cancelaNota(chave, numeroProtocolo, motivo);
    }

    /**
     * Faz o cancelamento da nota com evento ja assinado
     * ATENCAO: Esse metodo deve ser utilizado para assinaturas A3
     *
     * @param chave       chave de acesso da nota
     * @param eventoAssinadoXml evento ja assinado em formato XML
     * @return dados do cancelamento da nota retornado pelo webservice
     * @throws Exception caso nao consiga gerar o xml ou problema de conexao com o sefaz
     */
    public NFEnviaEventoRetorno cancelaNotaAssinada(final String chave, final String eventoAssinadoXml) throws Exception {
        return this.wsCancelamento.cancelaNotaAssinada(chave, eventoAssinadoXml);
    }

    /**
     * Inutiliza a nota com o evento assinado
     * ATENCAO: Esse metodo deve ser utilizado para assinaturas A3
     *
     * @param eventoAssinadoXml evento assinado em XML
     * @param modelo            modelo da nota (NF-e ou NFC-e)
     * @return dados da inutilizacao da nota retornado pelo webservice
     * @throws Exception caso nao consiga gerar o xml ou problema de conexao com o sefaz
     */
    public NFRetornoEventoInutilizacao inutilizaNotaAssinada(final String eventoAssinadoXml, final NFModelo modelo) throws Exception {
        return this.wsInutilizacao.inutilizaNotaAssinada(eventoAssinadoXml, modelo);
    }

    /**
     * Inutiliza a nota
     *
     * @param anoInutilizacaoNumeracao ano de inutilizacao
     * @param cnpjEmitente             CNPJ emitente da nota
     * @param serie                    serie da nota
     * @param numeroInicial            numero inicial da nota
     * @param numeroFinal              numero final da nota
     * @param justificativa            justificativa da inutilizacao
     * @param modelo                   modelo da nota (NF-e ou NFC-e)
     * @return dados da inutilizacao da nota retornado pelo webservice
     * @throws Exception caso nao consiga gerar o xml ou problema de conexao com o sefaz
     */
    public NFRetornoEventoInutilizacao inutilizaNota(final int anoInutilizacaoNumeracao, final String cnpjEmitente, final String serie, final String numeroInicial, final String numeroFinal, final String justificativa, final NFModelo modelo) throws Exception {
        return this.wsInutilizacao.inutilizaNota(anoInutilizacaoNumeracao, cnpjEmitente, serie, numeroInicial, numeroFinal, justificativa, modelo);
    }

    /**
     * Realiza a consulta de cadastro de pessoa juridica com inscricao estadual
     *
     * @param cnpj CNPJ da pessoa juridica
     * @param uf   UF da pessoa juridica
     * @return dados da consulta da pessoa juridica retornado pelo webservice
     * @throws Exception caso nao consiga gerar o xml ou problema de conexao com o sefaz
     */
    public NFRetornoConsultaCadastro consultaCadastro(final String cnpj, final NFUnidadeFederativa uf) throws Exception {
        return this.wsConsultaCadastro.consultaCadastro(cnpj, uf);
    }

    /**
     * Faz a manifestação do destinatário da nota
     *
     * @param chave chave de acesso da nota
     * @param tipoEvento  tipo do evento da manifestacao do destinatario
     * @param motivo      motivo do cancelamento
     * @param cnpj        cnpj do autor do evento
     * @return dados da manifestacao do destinatario da nota retornado pelo webservice
     * @throws Exception caso nao consiga gerar o xml ou problema de conexao com o sefaz
     */
    public NFEnviaEventoRetorno manifestaDestinatarioNota(final String chave, final NFTipoEventoManifestacaoDestinatario tipoEvento, final String motivo, final String cnpj) throws Exception {
        return this.wSManifestacaoDestinatario.manifestaDestinatarioNota(chave, tipoEvento, motivo, cnpj);
    }

    /**
     * Faz a manifestação do destinatário da nota com evento ja assinado
     * ATENCAO: Esse metodo deve ser utilizado para assinaturas A3
     *
     * @param chave       chave de acesso da nota
     * @param eventoAssinadoXml evento ja assinado em formato XML
     * @return dados da manifestacao do destinatario da nota retornado pelo webservice
     * @throws Exception caso nao consiga gerar o xml ou problema de conexao com o sefaz
     */
    public NFEnviaEventoRetorno manifestaDestinatarioNotaAssinada(final String chave, final String eventoAssinadoXml) throws Exception {
        return this.wSManifestacaoDestinatario.manifestaDestinatarioNotaAssinada(chave, eventoAssinadoXml);
    }

    /**
     * Faz o download do xml da nota para um cnpj
     * Informando até 10 chaves de acesso
     *
     * @param cnpj  para quem foi emitida a nota
     * @param chave chave de acesso da nota
     * @return dados do download da nota retornado pelo webservice
     * @throws Exception caso nao consiga gerar o xml ou problema de conexao com o sefaz
     */
    public NFDownloadNFeRetorno downloadNota(final String cnpj, final String chave) throws Exception {
        return this.wsNotaDownload.downloadNota(cnpj, chave);
    }
    
    /**
     * Disponibiliza para os atores da NF-e informacÌ§oÌƒes e documentos fiscais eletroÌ‚nicos de seu interesse.
     * A distribuicÌ§aÌƒo eÌ� realizada para emitentes, destinataÌ�rios, transportadores e terceiros informados no
     * conteuÌ�do da NF-e respectivamente no grupo do Emitente (tag:emit, id:C01), no grupo do DestinataÌ�rio
     * (tag:dest, id:E01), no grupo do Transportador (tag:transporta, id:X03) e no grupo de pessoas fiÌ�sicas
     * autorizadas a acessar o XML (tag:autXML, id:GA01)
     * ReferÃªncia: NT2014.002_v1.02_WsNFeDistribuicaoDFe
     *
     * @param cnpj
     * @param chave
     * @param nsu
     * @param unidadeFederativaAutorizador
     * @return
     * @throws Exception
     */
    public RetDistDFeInt consultaDocumentoFiscal(final String cnpj, final String chave, final String nsu, final NFUnidadeFederativa unidadeFederativaAutorizador) throws Exception {
        return this.wsDistribuicaoDocumentoFiscal.consultaDocumentoFiscal(cnpj, chave, nsu, unidadeFederativaAutorizador);
    }
}