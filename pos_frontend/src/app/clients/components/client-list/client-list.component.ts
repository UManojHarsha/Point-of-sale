import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Client } from '../../../interfaces/client.interface';
import { ClientService } from '../../../services/client.service';
import { ToastService } from '../../../services/toast.service';
import { AuthService } from '../../../services/auth.service';
import { Subject, debounceTime, distinctUntilChanged } from 'rxjs';

@Component({
  selector: 'app-client-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './client-list.component.html'
})
export class ClientListComponent implements OnInit {
  @Input() clients: Client[] = [];
  @Input() currentPage = 1;
  @Input() hasNextPage = false;
  @Input() loading = false;
  @Input() searchTerm = '';
  pageSize = 10;
  error: string | null = null;
  totalItems = 0;
  
  @Output() edit = new EventEmitter<Client>();
  @Output() pageChange = new EventEmitter<number>();
  @Output() searchChange = new EventEmitter<string>();
  @Output() addNew = new EventEmitter<void>();

  private searchSubject = new Subject<string>();

  constructor(
    private clientService: ClientService,
    private toastService: ToastService,
    public authService: AuthService
  ) {
    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(term => {
      this.currentPage = 1;
      if (term) {
        this.searchClients();
      } else {
        this.fetchClients();
      }
    });
  }

  ngOnInit() {
    this.fetchClients();
  }

  onEdit(client: Client) {
    this.edit.emit(client);
  }

  onPageChange(page: number) {
    this.currentPage = page;
    if (this.searchTerm) {
      this.searchClients();
    } else {
      this.fetchClients();
    }
  }

  onSearchInput(term: string) {
    this.searchTerm = term;
    this.searchSubject.next(term);
  }

  onAddNew() {
    this.addNew.emit();
  }

  public fetchClients() {
    this.loading = true;
    this.clientService.getClients(this.currentPage, this.pageSize).subscribe({
      next: (response) => {
        this.clients = response.data;
        this.totalItems = response.total;
        this.hasNextPage = response.hasNextPage;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error fetching clients:', error);
        this.error = error.error?.message || 'Failed to load clients';
        this.loading = false;
        this.toastService.show('Failed to load clients', 'error');
      }
    });
  }

  private searchClients() {
    this.loading = true;
    const searchValue = this.searchTerm.replace("%", "\\%").replace("_", "\\_");
    this.clientService.searchClients(searchValue, this.currentPage, this.pageSize)
      .subscribe({
        next: (response) => {
          this.clients = response.data;
          this.totalItems = response.total;
          this.hasNextPage = response.hasNextPage;
          this.loading = false;
        },
        error: (error) => {
          console.error('Error searching clients:', error);
          this.error = 'Failed to search clients';
          this.loading = false;
          this.toastService.show('Failed to search clients', 'error');
        }
      });
  }
} 