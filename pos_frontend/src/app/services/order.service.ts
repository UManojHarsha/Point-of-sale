import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Order , OrderResponse, OrderItemsData } from '../interfaces/order.interface';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private baseUrl = '${environment.apiUrl}/api';

  constructor(private http: HttpClient) {}

  getOrders(page: number = 1, pageSize: number = 10, search?: string): Observable<OrderResponse> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('pageSize', pageSize.toString());
    
    if (search) {
      params = params.set('search', search);
    }
    
    return this.http.get<OrderResponse>(`${this.baseUrl}/orders/`, { params });
  }

  getOrderDetails(orderId: number): Observable<OrderItemsData[]> {
    return this.http.get<OrderItemsData[]>(`${this.baseUrl}/orders/order-items/${orderId}`);
  }

  createOrder(order: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/orders/add`, order, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    });
  }

  createOrderItem(orderItem: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/orders/add`, orderItem);
  }

  getOrderPdf(orderId: number): Observable<string> {
    return this.http.get(`${this.baseUrl}/invoice/pdf/${orderId}`, { responseType: 'text' });
  }
  
  generateInvoice(orderId: number): Observable<any> {
    return this.http.post(`${this.baseUrl}/orders/${orderId}/generateInvoice`, {}, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        'Accept': 'text/plain'
      }),
      responseType: 'text'
    });
  }

  downloadInvoice(orderId: number): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/orders/${orderId}/downloadInvoice`, {
      responseType: 'blob',
      headers: new HttpHeaders({
        'Accept': 'application/pdf'
      })
    });
  }

} 
