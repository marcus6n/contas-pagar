import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Conta {
  id?: number;
  nome: string;
  valorOriginal: number;
  valorCorrigido?: number;
  diasAtraso?: number;
  dataPagamento: string;
  dataVencimento: string;
  percentualMulta?: number;
  percentualJurosDia?: number;
}

@Injectable({
  providedIn: 'root'
})
export class ContaService {
  private apiUrl = 'http://localhost:8080/api/contas';

  constructor(private http: HttpClient) { }

  criar(conta: Conta): Observable<Conta> {
    return this.http.post<Conta>(this.apiUrl, conta);
  }

  listar(): Observable<Conta[]> {
    return this.http.get<Conta[]>(this.apiUrl);
  }
}
