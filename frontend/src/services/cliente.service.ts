import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Cliente } from '../clases/dominio/cliente';
//import { saveAs } from 'file-saver';

@Injectable({
  providedIn: 'root'
})
export class ClienteService {

  constructor(private httpClient: HttpClient) { }

  public getClienteByDni(dni:number): Observable<Cliente> {
      return this.httpClient.get<Cliente>(`http://127.0.0.1:8080/cliente/${dni}`);
  }

  public postCliente(cliente:Cliente): Observable<Cliente> {
    return this.httpClient.post<Cliente>("http://127.0.0.1:8080/cliente",cliente);    
  }

  public updateCliente(cliente:Cliente): Observable<Cliente> {
    return this.httpClient.put<Cliente>(`http://127.0.0.1:8080/cliente`,cliente);    
  }
  public getClientes(tipoUsuario:number): Observable<Cliente[]> {
    return this.httpClient.get<Cliente[]>(`http://127.0.0.1:8080/cliente?tipoUsuario=${tipoUsuario}`);
  }
}
