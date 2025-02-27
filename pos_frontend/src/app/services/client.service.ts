import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Client } from '../interfaces/client.interface';
import { ClientForm } from '../interfaces/client-form.interface';

@Injectable({
  providedIn: 'root'
})
export class ClientService {
  private baseUrl = '${environment.apiUrl}/client';

  constructor(private http: HttpClient) {}

  getClients(page: number = 1, pageSize: number = 10): Observable<any> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('pageSize', pageSize.toString());
    
    return this.http.get(`${this.baseUrl}/`, { params });
  }

  addClient(client: ClientForm): Observable<any> {
    return this.http.post(`${this.baseUrl}/add`, client);
  }

  updateClient(id: number, client: ClientForm): Observable<any> {
    return this.http.put(`${this.baseUrl}/update/${id}`, client);
  }

  searchClients(searchTerm: string, page: number, pageSize: number): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('pageSize', pageSize.toString());
    
    if (searchTerm) {
      params = params.set('name', searchTerm);
    }

    return this.http.get<any>(`${this.baseUrl}/search`, { params });
  }
} 