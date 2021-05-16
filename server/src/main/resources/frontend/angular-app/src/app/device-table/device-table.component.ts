import { Component, OnInit } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { faCoffee, faDigitalTachograph } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-device-table',
  templateUrl: './device-table.component.html',
  styleUrls: ['./device-table.component.css']
})
export class DeviceTableComponent implements OnInit {

  constructor(private http: HttpClient) { }

  devices: any;

  faDigitalTachograph = faDigitalTachograph;

  ngOnInit(): void {

    this.http
      .get<string>("http://192.168.0.234:8182/bacnetz/api/device/all")
      .subscribe(
        (res) => {
            console.log("HTTP response", JSON.stringify(res));
            this.devices = res;
        },
        (err) => console.log("HTTP Error", err),
        () => console.log("complete")
      );

  }

}
