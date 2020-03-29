import {RouterModule, Routes} from "@angular/router";
import {NgModule} from "@angular/core";
import {LeagueComponent} from "./league.component";

const routes: Routes = [
    {
        path: '', component: LeagueComponent
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class LeagueRoutingModule { }
