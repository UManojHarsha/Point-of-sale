import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product } from '../interfaces/product.interface';
import { ProductForm } from '../interfaces/product-form.interface';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private baseUrl = '${environment.apiUrl}/product';

  constructor(private http: HttpClient) {}

  getProducts(page: number = 1, pageSize: number = 10): Observable<any> {
    return this.http.get(`${this.baseUrl}/`, {
      params: {
        page: page.toString(),
        pageSize: pageSize.toString()
      }
    });
  }

  addProduct(product: ProductForm): Observable<any> {
    return this.http.post(`${this.baseUrl}/add`, product);
  }

  updateProduct(id: number, product: ProductForm): Observable<any> {
    return this.http.put(`${this.baseUrl}/update/${id}`, product);
  }

  updateStock(product_id: number, stock: number): Observable<any> {
    return this.http.put(`${this.baseUrl}/update_stock/${product_id}`, { stock });
  }

  addBulkProducts(products: ProductForm[]): Observable<any> {
    return this.http.post(`${this.baseUrl}/add`, products);
  }

  searchProducts(term: string, page: number = 1, pageSize: number = 10): Observable<any> {
    console.log(term + "searching");
    const params = new HttpParams()
      .set('name', term)
      .set('page', page)
      .set('pageSize', pageSize)
    return this.http.get<any>(`${this.baseUrl}/search`, { params });
  }

  getProduct(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.baseUrl}/${id}`);
  }

  uploadProductMastersFile(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    
    return this.http.post(`${this.baseUrl}/uploadProductMasters`, formData, {
      reportProgress: true,
      observe: 'events'
    });
  }

  downloadTemplateTsv(): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/template`, {
      responseType: 'blob'
    });
  }
} 