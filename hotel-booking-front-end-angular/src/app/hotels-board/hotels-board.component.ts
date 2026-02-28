import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-hotels-board',
  templateUrl: './hotels-board.component.html',
  styleUrls: ['./hotels-board.component.css']
})
export class HotelsBoardComponent implements OnInit {
    images = [1, 2].map((n) => `assets/images/${n}.png`);
  constructor() { }

  ngOnInit(): void {
  }

}
