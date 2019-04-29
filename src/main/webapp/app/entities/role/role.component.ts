import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { IRole } from 'app/shared/model/role.model';
import { AccountService } from 'app/core';
import { RoleService } from './role.service';

@Component({
    selector: 'jhi-role',
    templateUrl: './role.component.html'
})
export class RoleComponent implements OnInit, OnDestroy {
    roles: IRole[];
    currentAccount: any;
    eventSubscriber: Subscription;
    currentSearch: string;

    constructor(
        protected roleService: RoleService,
        protected jhiAlertService: JhiAlertService,
        protected eventManager: JhiEventManager,
        protected activatedRoute: ActivatedRoute,
        protected accountService: AccountService
    ) {
        this.currentSearch =
            this.activatedRoute.snapshot && this.activatedRoute.snapshot.params['search']
                ? this.activatedRoute.snapshot.params['search']
                : '';
    }

    loadAll() {
        if (this.currentSearch) {
            this.roleService
                .search({
                    query: this.currentSearch
                })
                .pipe(
                    filter((res: HttpResponse<IRole[]>) => res.ok),
                    map((res: HttpResponse<IRole[]>) => res.body)
                )
                .subscribe((res: IRole[]) => (this.roles = res), (res: HttpErrorResponse) => this.onError(res.message));
            return;
        }
        this.roleService
            .query()
            .pipe(
                filter((res: HttpResponse<IRole[]>) => res.ok),
                map((res: HttpResponse<IRole[]>) => res.body)
            )
            .subscribe(
                (res: IRole[]) => {
                    this.roles = res;
                    this.currentSearch = '';
                },
                (res: HttpErrorResponse) => this.onError(res.message)
            );
    }

    search(query) {
        if (!query) {
            return this.clear();
        }
        this.currentSearch = query;
        this.loadAll();
    }

    clear() {
        this.currentSearch = '';
        this.loadAll();
    }

    ngOnInit() {
        this.loadAll();
        this.accountService.identity().then(account => {
            this.currentAccount = account;
        });
        this.registerChangeInRoles();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: IRole) {
        return item.id;
    }

    registerChangeInRoles() {
        this.eventSubscriber = this.eventManager.subscribe('roleListModification', response => this.loadAll());
    }

    protected onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }
}
