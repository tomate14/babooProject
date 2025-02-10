export interface Pago {
    id?:string;
    idPedido?: number;
    fechaPago: string;
    valor: number;
    formaPago?: number;
    descripcion?: string;
  }