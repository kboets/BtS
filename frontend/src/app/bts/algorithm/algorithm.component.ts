import {Component, OnInit} from "@angular/core";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AlgorithmService} from "./algorithm.service";
import {EMPTY, Subject} from "rxjs";
import {Algorithm} from "../domain/algorithm";
import {GeneralError} from "../domain/generalError";
import {catchError, debounceTime} from "rxjs/operators";
import {ConfirmationService, MessageService} from "primeng/api";

@Component({
    selector: 'bts-algorithm',
    providers: [ConfirmationService, MessageService],
    templateUrl: './algorithm.component.html'
})
export class AlgorithmComponent implements OnInit {

    private errorMessageSubject = new Subject<GeneralError>();
    errorMessage$ = this.errorMessageSubject.asObservable();

    public allButCurrentAlgorithms: Algorithm[];
    private allAlgorithms: Algorithm[];
    public currentAlgorithm: Algorithm;
    public addAlgorithm: boolean;
    public editAlgorithm: boolean;
    public viewOthers: boolean;
    public showDeleteMessage:boolean;
    newAlgorithm: Algorithm;
    submitted = false;
    crudAlgorithmForm: FormGroup;
    confirmationMessage: string;
    updateMessage: string
    isSavedOK: boolean;
    isUpdateOK: boolean;
    isEmptyAlgorithm: boolean;

    constructor(private fb: FormBuilder, private algorithmService: AlgorithmService, private confirmationService: ConfirmationService) {
        this.addAlgorithm = false;
        this.editAlgorithm = false;
        this.newAlgorithm = {} as Algorithm;
        this.confirmationMessage = 'Het algoritme werd opgeslagen en is nu de standaard';
        this.updateMessage = 'Het algoritme werd gewijzigd.';
        this.isSavedOK = false;
        this.isUpdateOK = false;
        this.isEmptyAlgorithm = true;
        this.allButCurrentAlgorithms = [];
        this.viewOthers = false;
        this.showDeleteMessage = false;

    }

    ngOnInit(): void {
        this.createNewForm();
        this.retrieveCurrentAlgorithm();
        this.retrieveAllButCurrentAlgorithm();
        this.retrieveAllAlgorithm();

        this.algorithmService.algorithmRefreshAction$
            .subscribe(()=> {
                this.retrieveCurrentAlgorithm();
                this.retrieveAllButCurrentAlgorithm();
                this.retrieveAllAlgorithm();
            });
    }

    private retrieveCurrentAlgorithm() {
        this.algorithmService.getCurrentAlgorithm()
            .pipe(catchError(err => {
                this.errorMessageSubject.next(err);
                return EMPTY;
            })
        ).subscribe((data) => {
            this.currentAlgorithm = data;
            if (data !== null) {
                this.isEmptyAlgorithm = false;
            }
        })
    }

    private retrieveAllButCurrentAlgorithm() {
        this.algorithmService.getAllButCurrentAlgorithms()
            .pipe(debounceTime(1000))
            .pipe(catchError(err => {
                    this.errorMessageSubject.next(err);
                    return EMPTY;
                })
            ).subscribe((data) => {
                this.allButCurrentAlgorithms = data;
                if (this.allButCurrentAlgorithms.length === 0) {
                    this.viewOthers = false;
                }
            })
    }

    private retrieveAllAlgorithm() {
        this.algorithmService.getAlgorithms()
            .pipe(debounceTime(1000))
            .pipe(catchError(err => {
                    this.errorMessageSubject.next(err);
                    return EMPTY;
                })
            ).subscribe((data) => {
                this.allAlgorithms = data;
        })
    }

    private createNewForm() {
        this.crudAlgorithmForm = this.fb.group({
            homeWin: 0,
            homeDraw: 0,
            homeLost: 0,
            homeBonus: 0,
            awayWin: 0,
            awayDraw: 0,
            awayLost: 0,
            awayMalus: 0,
            name: ['', Validators.required],
            current: {value: false},
            threshold: 0
        })
    }

    get addForm() { return this.crudAlgorithmForm.controls; }

    onAddSubmit() {
        this.submitted = true;
        if (this.crudAlgorithmForm.invalid) {
            return;
        }
        if (this.addAlgorithm) {
            this.setAlgorithmValues();
            if (this.verifyAlreadyExisting()) {
                let dataError = new GeneralError();
                dataError.userFriendlyMessage = 'Dit algoritme bestaat reeds.'
                this.errorMessageSubject.next(dataError);
            } else {
                this.saveAlgorithm();
            }
        } else {
            this.currentAlgorithm.threshold = this.crudAlgorithmForm.get('threshold').value;
            this.updateAlgorithm();
        }

    }

    private saveAlgorithm() {
        this.algorithmService.saveAlgorithm(this.newAlgorithm)
            .subscribe((data) => {
                this.removeAcknowledgeMessage();
                this.addAlgorithm = false;
                this.isSavedOK = true;
                this.isEmptyAlgorithm = false;
            })
    }

    private updateAlgorithm() {
        this.algorithmService.updateAlgorithm(this.currentAlgorithm)
            .subscribe((data) => {
                this.removeAcknowledgeMessage();
                this.editAlgorithm = false;
                this.isUpdateOK = true;
                this.isEmptyAlgorithm = false;
            })
    }

    private verifyAlreadyExisting(): boolean {
        let isSame = false;
        this.allAlgorithms.forEach(algorithm => {
            isSame = this.isSameAlgorithm(algorithm);
            if (isSame) {
                return true;
            }
        })
        return isSame;
    }

    onAddReset() {
        this.submitted = false;
        this.createNewForm();
    }

    openAddForm() {
        this.addAlgorithm = true;
        this.createNewForm();
    }

    openEditForm() {
        this.editAlgorithm = true;
        this.crudAlgorithmForm.patchValue(this.currentAlgorithm);
    }

    undoCrudForm() {
        this.addAlgorithm = false;
        this.editAlgorithm = false;
        this.viewOthers = false;
    }

    openOthers() {
        this.viewOthers = true;
    }

    setAlgorithmCurrent(algorithm: Algorithm) {
        algorithm.current = true;
        this.algorithmService.saveAlgorithm(algorithm)
            .subscribe((data) => {
                this.removeAcknowledgeMessage();
                this.addAlgorithm = false;
                this.isSavedOK = true;
                this.isEmptyAlgorithm = false;
            })
    }

    deleteAlgorithme(algorithm: Algorithm) {
        this.confirmationService.confirm({
            message: 'Verwijder dit algoritme ? Dit kan niet ongedaan gemaakt worden',
            header: 'Bevestiging',
            icon: 'pi pi-info-circle',
            accept: () => {
                this.algorithmService.deleteAlgorithm(algorithm)
                    .subscribe((data) => {
                        this.showDeleteMessage = true;
                        this.removeAcknowledgeMessage();
                    })
            }

        });
    }

    private setAlgorithmValues() {
        this.newAlgorithm = {} as Algorithm;
        this.newAlgorithm.homePoints = {
            'win' : this.crudAlgorithmForm.get('homeWin').value,
            'draw' : this.crudAlgorithmForm.get('homeDraw').value,
            'lose' : this.crudAlgorithmForm.get('homeLost').value
        }
        this.newAlgorithm.awayPoints = {
            'win' : this.crudAlgorithmForm.get('awayWin').value,
            'draw' : this.crudAlgorithmForm.get('awayDraw').value,
            'lose' : this.crudAlgorithmForm.get('awayLost').value
        }
        this.newAlgorithm.name = this.crudAlgorithmForm.get('name').value;
        this.newAlgorithm.threshold = this.crudAlgorithmForm.get('threshold').value;
        if (this.crudAlgorithmForm.get('current').value === true) {
            this.newAlgorithm.current = true;
        } else {
            this.newAlgorithm.current = true;
        }
        this.newAlgorithm.type = 'WIN';
        this.newAlgorithm.homeBonus = this.crudAlgorithmForm.get('homeBonus').value;
        this.newAlgorithm.awayMalus = this.crudAlgorithmForm.get('awayMalus').value;

    }

    private isSameAlgorithm(algorithm: Algorithm) {
        return this.newAlgorithm.homeBonus === algorithm.homeBonus
            && this.newAlgorithm.awayMalus === algorithm.awayMalus
            && this.newAlgorithm.awayPoints.win === algorithm.awayPoints.win
            && this.newAlgorithm.awayPoints.lose === algorithm.awayPoints.lose
            && this.newAlgorithm.awayPoints.draw === algorithm.awayPoints.draw
            && this.newAlgorithm.homePoints.win === algorithm.homePoints.win
            && this.newAlgorithm.homePoints.lose === algorithm.homePoints.lose
            && this.newAlgorithm.homePoints.draw === algorithm.homePoints.draw;
    }


    private removeAcknowledgeMessage() {
        setTimeout(() => {
            this.isSavedOK = false;
            this.isUpdateOK = false;
            this.showDeleteMessage = false;
        }, 5000);
    }

}
