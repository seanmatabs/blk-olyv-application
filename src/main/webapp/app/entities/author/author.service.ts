import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IAuthor } from 'app/shared/model/author.model';

type EntityResponseType = HttpResponse<IAuthor>;
type EntityArrayResponseType = HttpResponse<IAuthor[]>;

@Injectable({ providedIn: 'root' })
export class AuthorService {
    public resourceUrl = SERVER_API_URL + 'api/authors';
    public resourceSearchUrl = SERVER_API_URL + 'api/_search/authors';

    constructor(protected http: HttpClient) {}

    create(author: IAuthor): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(author);
        return this.http
            .post<IAuthor>(this.resourceUrl, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    update(author: IAuthor): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(author);
        return this.http
            .put<IAuthor>(this.resourceUrl, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    find(id: string): Observable<EntityResponseType> {
        return this.http
            .get<IAuthor>(`${this.resourceUrl}/${id}`, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http
            .get<IAuthor[]>(this.resourceUrl, { params: options, observe: 'response' })
            .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
    }

    delete(id: string): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    search(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http
            .get<IAuthor[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
            .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
    }

    protected convertDateFromClient(author: IAuthor): IAuthor {
        const copy: IAuthor = Object.assign({}, author, {
            created: author.created != null && author.created.isValid() ? author.created.toJSON() : null,
            lastActive: author.lastActive != null && author.lastActive.isValid() ? author.lastActive.toJSON() : null,
            birthday: author.birthday != null && author.birthday.isValid() ? author.birthday.toJSON() : null
        });
        return copy;
    }

    protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
        if (res.body) {
            res.body.created = res.body.created != null ? moment(res.body.created) : null;
            res.body.lastActive = res.body.lastActive != null ? moment(res.body.lastActive) : null;
            res.body.birthday = res.body.birthday != null ? moment(res.body.birthday) : null;
        }
        return res;
    }

    protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
        if (res.body) {
            res.body.forEach((author: IAuthor) => {
                author.created = author.created != null ? moment(author.created) : null;
                author.lastActive = author.lastActive != null ? moment(author.lastActive) : null;
                author.birthday = author.birthday != null ? moment(author.birthday) : null;
            });
        }
        return res;
    }
}
