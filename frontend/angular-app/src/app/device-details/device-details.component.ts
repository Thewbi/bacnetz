import { Component, OnInit, OnDestroy } from "@angular/core";
import { Router, ActivatedRoute, ParamMap } from "@angular/router";
import { HttpClient } from "@angular/common/http";

@Component({
  selector: "app-device-details",
  templateUrl: "./device-details.component.html",
  styleUrls: ["./device-details.component.css"],
})
export class DeviceDetailsComponent implements OnInit {

  public id: number;

  public device: any;

  //backendUrl = "http://192.168.0.234:8182/bacnetz/api";
  backendUrl = "http://127.0.0.1:8182/bacnetz/api";

  constructor(private route: ActivatedRoute, private http: HttpClient) {}

  ngOnInit(): void {
    // this.sub = this.route.queryParams.subscribe((params) => {
    //   console.log(params);
    //   this.id = Number(params['id']);
    // });

    // https://www.samjulien.com/how-to-use-route-parameters-in-angular
    // The route snapshot provides the initial value of the route parameter map (called the paramMap).
    //this.id = Number(this.route.snapshot.paramMap.get('id'));

    // a subscription to the paramMap is better because???
    this.route.paramMap.subscribe((params: ParamMap) => {
      this.id = +params.get('id')
    })

    console.log("Displaying details of id: " + this.id);

    this.http
      .get<string>(this.backendUrl + "/device/details/" + this.id)
      .subscribe(
        (res) => {
            console.log("HTTP response", JSON.stringify(res));
            this.device = res;
        },
        (err) => console.log("HTTP Error", err),
        () => console.log("complete")
      );
  }

  ngOnDestroy() {

  }
}
