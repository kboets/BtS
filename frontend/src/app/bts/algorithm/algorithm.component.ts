import {Component, OnInit} from "@angular/core";
import {FormBuilder, FormGroup} from "@angular/forms";
@Component({
    selector: 'bts-algorithm',
    templateUrl: './algorithm.component.html'
})
export class AlgorithmComponent implements OnInit {

    algorithmForm: FormGroup;
    private algorithm;

    constructor(private fb: FormBuilder) {

    }
    ngOnInit(): void {
        this.algorithmForm = this.fb.group({

        })


    }

}
