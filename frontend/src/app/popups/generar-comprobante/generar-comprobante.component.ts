import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SearchableSelectComponent } from "../../generales/searchable-select/searchable-select.component";
import { ClienteService } from '../../../services/cliente.service';
import { Cliente } from '../../../clases/dominio/cliente';
import { CurrencyPipe, NgClass, NgFor, NgIf } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Producto } from '../../../clases/dominio/producto';
import { ProductoService } from '../../../services/producto.service';
import { ConfirmarService } from '../../../services/popup/confirmar';
import { PedidosService } from '../../../services/pedidos.service';
import { FormaDePago, formaDePago } from '../../../clases/constantes/formaPago';

@Component({
  selector: 'app-generar-comprobante',
  standalone: true,
  imports: [SearchableSelectComponent, NgIf, ReactiveFormsModule, FormsModule, NgClass, NgFor, CurrencyPipe],
  templateUrl: './generar-comprobante.component.html',
  styleUrl: './generar-comprobante.component.css'
})
export class GenerarComprobanteComponent {
  
  tipoComprobante:string | undefined;
  tipoUsuario:number = 0;
  usuarios: Cliente[] = [];
  productosProveedor:Producto[] = [];
  productos:Producto[] = [];
  proveedor:Cliente | undefined;
  myForm: FormGroup;
  readonly:boolean = false;
  formaDePago:FormaDePago[] = [];
  selectedFormaDePago:number = 1;
  constructor(private route: ActivatedRoute, private clienteService: ClienteService, private fb: FormBuilder,
    private productoService:ProductoService, private confirmarService:ConfirmarService, private pedidoService: PedidosService
  ) {
    this.formaDePago = formaDePago;
    this.route.params.subscribe(params => {
      this.tipoComprobante = params['tipo'];
      if (this.tipoComprobante === 'ORC') {
        this.tipoUsuario = 2;
        this.clienteService.getClientes(this.tipoUsuario).subscribe(res => {
          this.usuarios = res;
        })
      }
    });
    this.myForm = this.fb.group({  
      id: null,    
      codigoBarra: null,      
      stock: [null, Validators.required],
      precioCompra: [null, Validators.required],
      nombre: [null, Validators.required]
    });
  }
  
  onProveedorSelect(cliente: Cliente) {
    console.log(cliente);
    this.proveedor = cliente;
    if (cliente ) {
      const filtro = `idProveedor=${cliente.dni}`;
      this.productoService.getByParams([filtro]).subscribe((res:Producto[]) =>{
        this.productosProveedor = res;
      })
    }
  }
  onProductoSelect(producto: Producto) {
    this.myForm.patchValue({
      id: producto.id,
      nombre: producto.nombre,
      codigoBarra: producto.codigoBarra,
      precioCompra: producto.precioCompra
    });
    //this.readonly = true;
  }
  onSubmit() {
    if (this.myForm.valid) {
      const existente = this.productos.filter(prod => prod.id).find(prod => prod.id === this.myForm.value.id);
      if (!existente) {
        const producto:Producto = {
          id: this.myForm.value.id,
          nombre: this.myForm.value.nombre,
          codigoBarra: this.myForm.value.codigoBarra,
          precioCompra: this.myForm.value.precioCompra,
          stock: this.myForm.value.stock,
          idProveedor: this.proveedor ? this.proveedor.dni : 0
        }
        this.productos.push(producto);
        this.myForm.reset();
        //this.readonly = false;
      } else {
        this.confirmarService.confirm('Error', 'No se puede agregar', true, 'Aceptar', 'Cancelar');
      }
    }
  }

  guardarProductos() {
    const total = this.calcularTotal();
    this.pedidoService.crearPedido(this.productos, 'ORC', this.selectedFormaDePago, total).subscribe((res:any)=> {
      console.log(res);
      this.productos = [];
      this.productosProveedor = [];
      //this.readonly = false;
    }, (error) => {
      this.confirmarService.confirm('Error', error.message, true, 'Aceptar', 'Cancelar');
    })
  }

  onFormaDePagoChange(event: Event) {
    const target = event.target as HTMLSelectElement;
    this.selectedFormaDePago = +target.value;
    console.log('Forma de pago seleccionada:', this.selectedFormaDePago);
  }

  calcularTotal() {
    return this.productos.reduce((accum, producto) => accum + (producto.precioCompra * producto.stock), 0);
  }
  editarProducto(_t58: Producto) {
    throw new Error('Method not implemented.');
  }

}
