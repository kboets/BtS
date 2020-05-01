import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {PageHeaderModule} from "../../shared/modules";
import {TeamsRoutingModule} from "./teams-routing.module";
import {TeamsComponent} from "./teams.component";

@NgModule({
    imports: [CommonModule, TeamsRoutingModule, PageHeaderModule],
    declarations: [TeamsComponent]
})
export class TeamsModule{}
