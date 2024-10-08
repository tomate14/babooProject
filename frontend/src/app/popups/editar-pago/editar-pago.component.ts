import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Pago } from '../../../clases/dominio/pago';
import { PagosService } from '../../../services/pago.service';
import { HttpClient } from '@angular/common/http';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { FormaDePago, formaDePago } from '../../../clases/constantes/formaPago';
import { NgClass, NgFor, NgIf } from '@angular/common';
import { nowConLuxonATimezoneArgentina } from '../../../utils/dates';
import { esIngresoEgresoCaja } from '../../../utils/funciones';

@Component({
  selector: 'app-editar-pedido',
  standalone: true,
  imports: [NgIf, NgFor, NgClass, ReactiveFormsModule, FormsModule],
  templateUrl: './editar-pago.component.html',
  styleUrl: './editar-pago.component.css'
})
export class EditarPagoComponent implements OnInit{

  myForm: FormGroup;
  @Input() pago:Pago | undefined;
  formaDePago:FormaDePago[] = [];

  constructor(private fb: FormBuilder, private pagosService: PagosService, private httpClient: HttpClient, private activeModal: NgbActiveModal) {    

      this.myForm = this.fb.group({}); 
  }

  ngOnInit(): void {
    this.formaDePago = formaDePago;
    if (this.pago) {
      this.myForm = this.fb.group({      
        descripcion: [this.pago.descripcion, Validators.required],      
        total: [this.pago.valor, Validators.required],
        formaDePago: [this.pago.formaPago, Validators.required]
      }); 
    }
  }
  onSubmit() {
    if (this.myForm.valid) {
      const fechaPago = this.pago?.fechaPago  as unknown as string;
      const pago:Pago = {
        fechaPago: fechaPago,
        valor: this.myForm.value.total,
        descripcion: this.myForm.value.descripcion,
        idPedido: this.pago?.idPedido,
        formaPago: +this.myForm.value.formaDePago
      }
      const idPago = this.pago?.id  as unknown as string;
      if (idPago) {
        this.pagosService.putPago(idPago, pago).subscribe(res => {
          pago.id = this.pago?.id;
          this.activeModal.close(pago);
        }, (error) => {
          alert(error.error.message);
        })
      }
    } else {
      console.log('Form Not Valid');
    }
  }

  esIngresoEgresoCaja(pago: Pago | undefined): any {
    if (pago) {
      return esIngresoEgresoCaja(pago);
    }
    return false;
  }

  cerrar() {
    this.myForm.reset();
    this.activeModal.close(false);
  }
}
