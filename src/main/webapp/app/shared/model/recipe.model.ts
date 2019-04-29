import { IImage } from 'app/shared/model/image.model';
import { IStep } from 'app/shared/model/step.model';
import { IAuthor } from 'app/shared/model/author.model';
import { IIngredient } from 'app/shared/model/ingredient.model';
import { ICategory } from 'app/shared/model/category.model';

export interface IRecipe {
    id?: string;
    name?: string;
    description?: string;
    serves?: number;
    preptime?: string;
    images?: IImage[];
    steps?: IStep[];
    author?: IAuthor;
    ingredient?: IIngredient;
    categories?: ICategory[];
}

export class Recipe implements IRecipe {
    constructor(
        public id?: string,
        public name?: string,
        public description?: string,
        public serves?: number,
        public preptime?: string,
        public images?: IImage[],
        public steps?: IStep[],
        public author?: IAuthor,
        public ingredient?: IIngredient,
        public categories?: ICategory[]
    ) {}
}
