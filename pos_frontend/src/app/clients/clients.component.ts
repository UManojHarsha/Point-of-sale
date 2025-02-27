import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { Client } from '../interfaces/client.interface';
import { ClientForm } from '../interfaces/client-form.interface';
import { ClientService } from '../services/client.service';
import { ToastService } from '../services/toast.service';
import { Subject, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { ClientListComponent } from './components/client-list/client-list.component';
import { AddClientComponent } from './components/add-client/add-client.component';
import { EditClientComponent } from './components/edit-client/edit-client.component';

@Component({
  selector: 'app-clients',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ClientListComponent,
    AddClientComponent,
    EditClientComponent
  ],
  templateUrl: './clients.component.html',
  styleUrls: ['./clients.component.scss']
})
export class ClientsComponent implements OnInit, OnDestroy {
  @ViewChild(ClientListComponent) private clientList!: ClientListComponent;

  protected readonly Math = Math;
  clients: Client[] = [];
  currentPage = 1;
  pageSize = 10;
  totalItems = 0;
  loading = false;
  error: string | null = null;
  editingClient: Client | null = null;
  showAddForm = false;
  newClient: ClientForm = {
    name: '',
    email: '',
    contactNo: ''
  };
  searchTerm = '';
  private searchSubject = new Subject<string>();
  private searchSubscription?: Subscription;
  hasNextPage = false;

  constructor(
    private clientService: ClientService,
    private toastService: ToastService
  ) {}

  ngOnInit() {
    this.fetchClients(); 
  }

  ngOnDestroy() {
    if (this.searchSubscription) {
      this.searchSubscription.unsubscribe();
    }
  }

  onSearchInput(event: string) {
    this.searchTerm = event;
    this.currentPage = 1;
    if (this.searchTerm) {
      this.searchClients();
    } else {
      this.fetchClients();
    }
  }

  fetchClients() {
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

  startEdit(client: Client) {
    this.editingClient = { ...client };
    this.toastService.show('Editing client: ' + client.name, 'info');
  }

  cancelEdit() {
    this.editingClient = null;
    this.toastService.show('Cancelled editing client', 'info');
  }

  saveEdit() {
    this.editingClient = null;
    this.clientList.fetchClients();
  }

  addNewClient() {
    this.showAddForm = true;
    this.toastService.show('Opening add client form', 'info');
    this.newClient = {
      name: '',
      email: '',
      contactNo: ''
    };
  }

  cancelAdd() {
    this.showAddForm = false;
    this.toastService.show('Cancelled adding client', 'info');
  }

  submitNewClient() {
    this.showAddForm = false;
    this.clientList.fetchClients();
  }

  get filteredClients(): Client[] {
    return this.clients.filter((client: Client) => 
      client.name.toLowerCase().includes(this.searchTerm.toLowerCase())
    );
  }

  onPageChange(page: number) {
    this.currentPage = page;
    if (this.searchTerm) {
      this.searchClients();
    } else {
      this.fetchClients();
    }
  }

  private searchClients() { 
    this.loading = true;
    const searchValue = this.searchTerm.replace("%", "\\%").replace("_", "\\_");
    this.clientService.searchClients(this.searchTerm, this.currentPage, this.pageSize)
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
        }
      });
  }
}
