import { IRecipe } from 'app/shared/model/recipe.model';

export interface IStep {
    id?: string;
    title?: string;
    description?: string;
    recipe?: IRecipe;
}

export class Step implements IStep {
    constructor(public id?: string, public title?: string, public description?: string, public recipe?: IRecipe) {}
}
