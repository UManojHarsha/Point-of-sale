import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DailySalesData } from '../interfaces/daily-sales.interface';

@Injectable({
  providedIn: 'root'
})
export class DailySalesService {
  private baseUrl = '${environment.apiUrl}/api';

  constructor(private http: HttpClient) {}

  getDailySales(startDate?: Date, endDate?: Date): Observable<DailySalesData[]> {
    let params = new HttpParams();
    if (startDate) {
      params = params.set('startDate', startDate.toISOString());
    }
    if (endDate) {
      params = params.set('endDate', endDate.toISOString());
    }

    return this.http.get<DailySalesData[]>(`${this.baseUrl}/orders/daily-sales`, { params });
  }
} 