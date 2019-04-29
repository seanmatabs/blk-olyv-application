import { IStep } from 'app/shared/model/step.model';
import { IAuthor } from 'app/shared/model/author.model';
import { ICategory } from 'app/shared/model/category.model';
import { IIngredient } from 'app/shared/model/ingredient.model';

export interface IRecipe {
    id?: string;
    name?: string;
    description?: string;
    serves?: number;
    preptime?: string;
    steps?: IStep[];
    author?: IAuthor;
    categories?: ICategory[];
    ingredients?: IIngredient[];
}

export class Recipe implements IRecipe {
    constructor(
        public id?: string,
        public name?: string,
        public description?: string,
        public serves?: number,
        public preptime?: string,
        public steps?: IStep[],
        public author?: IAuthor,
        public categories?: ICategory[],
        public ingredients?: IIngredient[]
    ) {}
}
