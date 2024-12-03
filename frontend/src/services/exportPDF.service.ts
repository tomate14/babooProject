import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Caja } from '../clases/dominio/caja';
import {BACKEND_URL} from "../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class ExportPDFService {

  constructor(private httpClient: HttpClient) { }

  public getDocumentoPDF(idPedido:string): any {
    return this.httpClient.get(`${BACKEND_URL}/informes?idPedido=${idPedido}`, {
        responseType: 'blob', // Especificar que la respuesta es un Blob
    });
  }
}
