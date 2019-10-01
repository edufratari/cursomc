package com.eduardofratari.cursomc.services;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eduardofratari.cursomc.domain.ItemPedido;
import com.eduardofratari.cursomc.domain.PagamentoComBoleto;
import com.eduardofratari.cursomc.domain.Pedido;
import com.eduardofratari.cursomc.domain.enums.EstadoPagamento;
import com.eduardofratari.cursomc.repositories.ItemPedidoRepository;
import com.eduardofratari.cursomc.repositories.PagamentoRepository;
import com.eduardofratari.cursomc.repositories.PedidoRepository;
import com.eduardofratari.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class PedidoService {

	@Autowired
	private PedidoRepository repo;

	@Autowired
	private BoletoService boletoService;

	@Autowired
	private PagamentoRepository pagamentoRepository;

	@Autowired
	private ProdutoService produtoService;

	@Autowired
	private ItemPedidoRepository itemPedidoRepository;

	public Pedido find(Integer id) {
		Optional<Pedido> obj = repo.findById(id);

		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Pedido.class.getName()));
	}

	@Transactional
	public Pedido insert(Pedido obj) {
		obj.setId(null);
		obj.setInstante(new Date());
		obj.getPagamento().setEstado(EstadoPagamento.PENDENTE);
		obj.getPagamento().setPedido(obj);

		if (obj.getPagamento() instanceof PagamentoComBoleto) {
			PagamentoComBoleto pagto = (PagamentoComBoleto) obj.getPagamento();
			boletoService.preencherPagamentoComBoleto(pagto, obj.getInstante());
		}

		obj = repo.save(obj);
		pagamentoRepository.save(obj.getPagamento());
		
		for (ItemPedido itemPedido : obj.getItens()) {
			itemPedido.setDesconto(0.0);
			itemPedido.setPreco(produtoService.find(itemPedido.getProduto().getId()).getPreco());
			itemPedido.setPedido(obj);
		}

		itemPedidoRepository.saveAll(obj.getItens());

		return obj;
	}
}
