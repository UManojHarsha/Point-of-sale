import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Inventory, InventoryResponse } from '../interfaces/inventory.interface';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class InventoryService {
  private apiUrl = `${environment.apiUrl}/inventory`;

  constructor(private http: HttpClient) {}

  getInventory(page: number, pageSize: number): Observable<InventoryResponse> {
    return this.http.get<InventoryResponse>(`${this.apiUrl}?page=${page}&pageSize=${pageSize}`);
  }

  searchInventory(term: string, page: number, pageSize: number): Observable<InventoryResponse> {
    return this.http.get<InventoryResponse>(`${this.apiUrl}/search?term=${term}&page=${page}&pageSize=${pageSize}`);
  }

  updateInventory(id: number, data: Partial<Inventory>): Observable<Inventory> {
    return this.http.put<Inventory>(`${this.apiUrl}/${id}`, data);
  }

  importInventory(data: FormData): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(`${this.apiUrl}/import`, data);
  }

  uploadInventoryMastersFile(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    
    return this.http.post(`${this.apiUrl}/uploadInventoryMasters`, formData, {
      reportProgress: true,
      observe: 'events'
    });
  }

  downloadTemplateTsv(): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/template`, {
      responseType: 'blob'
    });
  }
}
