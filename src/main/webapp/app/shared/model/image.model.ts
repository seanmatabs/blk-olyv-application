import { IRecipe } from 'app/shared/model/recipe.model';

export interface IImage {
    id?: string;
    name?: string;
    url?: string;
    recipe?: IRecipe;
}

export class Image implements IImage {
    constructor(public id?: string, public name?: string, public url?: string, public recipe?: IRecipe) {}
}
