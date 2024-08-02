import { Component } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Producto } from '../../../clases/dominio/producto';
import { ConfirmarService } from '../../../services/popup/confirmar';
import { CurrencyPipe, NgClass, NgFor, NgIf, registerLocaleData } from '@angular/common';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { NgxPaginationModule } from 'ngx-pagination';
import { ProductoService } from '../../../services/producto.service';

import localeEsAr from '@angular/common/locales/es-AR';
import localeEsArExtra from '@angular/common/locales/extra/es-AR';
import { CrearProductoService } from '../../../services/popup/crearProducto.service';
registerLocaleData(localeEsAr, 'es-AR', localeEsArExtra);
@Component({
  selector: 'app-tabla-productos',
  standalone: true,
  imports: [NgFor, NgbModule, NgIf, ReactiveFormsModule, FormsModule, NgxPaginationModule, NgClass, CurrencyPipe],
  templateUrl: './tabla-productos.component.html',
  styleUrl: './tabla-productos.component.css'
})
export class TablaProductoComponent {

  productos: Producto[] = [];
  filterForm: FormGroup;
  isCollapsed: boolean = true;
  tipoUsuario:number= 1;
  p: number = 1;
  constructor(private route: ActivatedRoute, private fb: FormBuilder, private productosService: ProductoService,
    private crearProductoService: CrearProductoService, private confirmarService:ConfirmarService) {
    this.filterForm = this.fb.group({
      id: [''],
      nombre: ['']
    });
  }

  ngOnInit(): void {
    this.productosService.getByParams([]).subscribe((data: Producto[]) => {
        this.productos = data;
      },(error) => {
        console.error('Error al obtener los datos de los productos', error);
      }
    );
  }

  crearProducto(): void {
    /*const cliente = undefined;
    this.crearProductoService.crearProducto(cliente)
    .then((cliente) => {
        if(cliente){
          this.limpiar();
        }
    });*/
  }

  /*verPedidos(cliente: Producto): void {
    this.pedidosService.getByDniProducto(cliente.dni).subscribe((res) => {
      this.listaPedidosService.crearLista(cliente.dni, res).then((confirmed) => {
        if(confirmed){
          console.log("Listado")
        }
      });
      // Lógica para ver detalles del cliente
      console.log('Pedidos por cliente:', res);
    }, (error) => {
      this.confirmarService.confirm("Producto sin pedidos", error.error.message, true,"Ok", "No");
    })    
  }*/
  editarProducto(producto:Producto) {
    this.crearProductoService.abrirProducto(producto)
    .then((producto:Producto)  => {
      if (producto) {
        this.limpiar();
      }
    });
  }

  buscar() {
    const { id, nombre } = this.filterForm.value;
    let params = [];
    if (id) {
      params.push("id="+id);
    }
    if (nombre) {
      params.push("nombre="+nombre);
    }

    this.productosService.getByParams(params).subscribe((res) => {
      this.productos = res;
    }, (error) => {
      this.productos = []
    });
  }
  limpiar() {
    this.filterForm.reset();
    this.productosService.getByParams([]).subscribe(
      (data: Producto[]) => {
        this.productos = data;
      },
      (error) => {
        this.confirmarService.confirm('Error', error.error.message, true, 'Aceptar', '');
      }
    );
  }
}
