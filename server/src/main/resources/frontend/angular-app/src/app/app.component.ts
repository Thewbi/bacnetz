import { Component } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  title = 'angular-app';

  constructor(private http: HttpClient) { }

  onClickMe() {
    console.log('click');

    // get with URL params
    let params = new HttpParams().set('logNamespace', 'logNamespace');

    this.http.get<string>('http://127.0.0.1:8182/bacnetz/sysinfo/version', {params: params})
    .subscribe(
        res => console.log('HTTP response', res),
        err => console.log('HTTP Error', err),
        () => console.log('complete')
    );

    let urlSearchParams = new URLSearchParams();
    urlSearchParams.append('uid', '101');
    const httpOptions = {
        params: { uid: 101}
    };

    // post with body
    // this.http.post('http://127.0.0.1:8182/bacnetz/device/toggle/', JSON.stringify({
    //   username: 'username',
    //   password: 'password',
    // })).subscribe(
    //     res => console.log('HTTP response', res),
    //     err => console.log('HTTP Error', err),
    //     () => console.log('complete')
    // );

    // post with body and URL params
    // this.http.post('http://127.0.0.1:8182/bacnetz/device/toggle', JSON.stringify({
    //   username: 'username',
    //   password: 'password',
    // }), {params: params}).subscribe(
    //     res => console.log('HTTP response', res),
    //     err => console.log('HTTP Error', err),
    //     () => console.log('complete')
    // );

    // post with path parameter
    const url = 'http://127.0.0.1:8182/bacnetz/device/toggle/' + 101;
    this.http.post(url, {}).subscribe(
        res => console.log('HTTP response', res),
        err => console.log('HTTP Error', err),
        () => console.log('complete')
    );
  }
}
