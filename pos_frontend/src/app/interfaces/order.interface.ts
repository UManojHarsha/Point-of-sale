export enum OrderStatus {
  PENDING = 'PENDING_INVOICE',
  PROCESSING = 'INVOICE_GENERATED',
  COMPLETED = 'COMPLETED',
}

export interface Order {
  id: number;
  userEmail: string;
  updatedDate: Date;
  totalPrice: number;
  status: OrderStatus;
}

export interface OrderResponse {
  data: Order[];
  total: number;
  hasNextPage: boolean;
}

export interface OrderForm {
  userEmail: string;
  totalPrice: number;
  productId: number;
  quantity: number;
}

// export interface OrderDetail {
//   id: number;
//   orderId: number;
//   productName: string;
//   quantity: number;
//   price: number;
// }

export interface OrderItemsData {
  id: number;
  orderId: number;
  productName: string;
  quantity: number;
  price: number;
} 