import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IStep } from 'app/shared/model/step.model';

type EntityResponseType = HttpResponse<IStep>;
type EntityArrayResponseType = HttpResponse<IStep[]>;

@Injectable({ providedIn: 'root' })
export class StepService {
    public resourceUrl = SERVER_API_URL + 'api/steps';
    public resourceSearchUrl = SERVER_API_URL + 'api/_search/steps';

    constructor(protected http: HttpClient) {}

    create(step: IStep): Observable<EntityResponseType> {
        return this.http.post<IStep>(this.resourceUrl, step, { observe: 'response' });
    }

    update(step: IStep): Observable<EntityResponseType> {
        return this.http.put<IStep>(this.resourceUrl, step, { observe: 'response' });
    }

    find(id: string): Observable<EntityResponseType> {
        return this.http.get<IStep>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IStep[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: string): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    search(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IStep[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
    }
}
