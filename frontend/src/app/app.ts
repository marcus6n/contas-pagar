import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ContaService, Conta } from './services/conta.service';

@Component({
  selector: 'app-root',
  imports: [CommonModule, FormsModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  contas: Conta[] = [];
  novaConta: Conta = {
    nome: '',
    valorOriginal: 0,
    dataVencimento: '',
    dataPagamento: ''
  };
  
  mensagem: string = '';
  erro: string = '';

  constructor(private contaService: ContaService) {}

  ngOnInit() {
    this.carregarContas();
  }

  carregarContas() {
    this.contaService.listar().subscribe({
      next: (data) => {
        this.contas = data;
      },
      error: (err) => {
        this.erro = 'Erro ao carregar contas';
        console.error(err);
      }
    });
  }

  salvar() {
    this.erro = '';
    this.mensagem = '';

    if (!this.validarFormulario()) {
      return;
    }

    this.contaService.criar(this.novaConta).subscribe({
      next: (conta) => {
        this.mensagem = 'Conta cadastrada com sucesso!';
        this.contas.push(conta);
        this.limparFormulario();
        setTimeout(() => this.mensagem = '', 3000);
      },
      error: (err) => {
        this.erro = 'Erro ao cadastrar conta: ' + (err.error?.message || 'Erro desconhecido');
        console.error(err);
      }
    });
  }

  validarFormulario(): boolean {
    if (!this.novaConta.nome || this.novaConta.nome.trim() === '') {
      this.erro = 'Nome é obrigatório';
      return false;
    }
    if (!this.novaConta.valorOriginal || this.novaConta.valorOriginal <= 0) {
      this.erro = 'Valor original deve ser maior que zero';
      return false;
    }
    if (!this.novaConta.dataVencimento) {
      this.erro = 'Data de vencimento é obrigatória';
      return false;
    }
    if (!this.novaConta.dataPagamento) {
      this.erro = 'Data de pagamento é obrigatória';
      return false;
    }
    return true;
  }

  limparFormulario() {
    this.novaConta = {
      nome: '',
      valorOriginal: 0,
      dataVencimento: '',
      dataPagamento: ''
    };
  }
}
