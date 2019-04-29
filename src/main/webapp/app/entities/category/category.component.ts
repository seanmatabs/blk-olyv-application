import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { ICategory } from 'app/shared/model/category.model';
import { AccountService } from 'app/core';
import { CategoryService } from './category.service';

@Component({
    selector: 'jhi-category',
    templateUrl: './category.component.html'
})
export class CategoryComponent implements OnInit, OnDestroy {
    categories: ICategory[];
    currentAccount: any;
    eventSubscriber: Subscription;
    currentSearch: string;

    constructor(
        protected categoryService: CategoryService,
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
            this.categoryService
                .search({
                    query: this.currentSearch
                })
                .pipe(
                    filter((res: HttpResponse<ICategory[]>) => res.ok),
                    map((res: HttpResponse<ICategory[]>) => res.body)
                )
                .subscribe((res: ICategory[]) => (this.categories = res), (res: HttpErrorResponse) => this.onError(res.message));
            return;
        }
        this.categoryService
            .query()
            .pipe(
                filter((res: HttpResponse<ICategory[]>) => res.ok),
                map((res: HttpResponse<ICategory[]>) => res.body)
            )
            .subscribe(
                (res: ICategory[]) => {
                    this.categories = res;
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
        this.registerChangeInCategories();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: ICategory) {
        return item.id;
    }

    registerChangeInCategories() {
        this.eventSubscriber = this.eventManager.subscribe('categoryListModification', response => this.loadAll());
    }

    protected onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }
}
