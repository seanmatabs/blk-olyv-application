import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
    imports: [
        RouterModule.forChild([
            {
                path: 'ingredient',
                loadChildren: './ingredient/ingredient.module#BlkOlyvAppIngredientModule'
            },
            {
                path: 'image',
                loadChildren: './image/image.module#BlkOlyvAppImageModule'
            },
            {
                path: 'location',
                loadChildren: './location/location.module#BlkOlyvAppLocationModule'
            },
            {
                path: 'recipe',
                loadChildren: './recipe/recipe.module#BlkOlyvAppRecipeModule'
            },
            {
                path: 'category',
                loadChildren: './category/category.module#BlkOlyvAppCategoryModule'
            },
            {
                path: 'step',
                loadChildren: './step/step.module#BlkOlyvAppStepModule'
            },
            {
                path: 'role',
                loadChildren: './role/role.module#BlkOlyvAppRoleModule'
            },
            {
                path: 'author',
                loadChildren: './author/author.module#BlkOlyvAppAuthorModule'
            }
            /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
        ])
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class BlkOlyvAppEntityModule {}
