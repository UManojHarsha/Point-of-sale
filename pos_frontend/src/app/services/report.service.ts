import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ReportForm, ReportData } from '../interfaces/report.interface';

@Injectable({
  providedIn: 'root'
})
export class ReportService {
  private baseUrl = '${environment.apiUrl}/api';

  constructor(private http: HttpClient) {}

  getReport(filters: ReportForm): Observable<ReportData[]> {
    return this.http.post<ReportData[]>(`${this.baseUrl}/orders/report`, filters);
  }
} 