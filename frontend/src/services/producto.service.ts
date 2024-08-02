import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Producto } from '../clases/dominio/producto';

@Injectable({
  providedIn: 'root'
})
export class ProductoService {

  constructor(private httpClient: HttpClient) { }

  public getProductoById(id:number): Observable<Producto> {
      return this.httpClient.get<Producto>(`http://127.0.0.1:8080/producto?id=${id}`);
  }

  public getByParams(params:string[]): Observable<Producto[]> {
    let url = `http://127.0.0.1:8080/producto`;
    if (params.length > 0) {
      url = url + '?'+params.join("&");
    }
    return this.httpClient.get<Producto[]>(url);
    }

  public crearProdcuto(producto:Producto): Observable<Producto> {
    return this.httpClient.post<Producto>("http://127.0.0.1:8080/producto",producto);    
  }

  public updateProducto(producto:Producto): Observable<Producto> {
    return this.httpClient.put<Producto>(`http://127.0.0.1:8080/producto`,producto);    
  }
}
