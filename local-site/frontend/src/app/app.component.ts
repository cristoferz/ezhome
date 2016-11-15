import { Component } from '@angular/core';
import { MessagesModule, Message } from 'primeng/primeng';

@Component({
   selector: 'app-root',
   templateUrl: './app.component.html',
   styleUrls: ['./app.component.css']
})
export class AppComponent {
   title = 'app works!';
   msgs: Message[];
   message: string;

   ngOnInit() {
      this.msgs = new Array<Message>();
   }

   public showMessage() {
      this.msgs.push({severity:'info', summary:'Info Message', detail:'PrimeNG rocks'});
      setTimeout(() => {
         this.msgs.pop();
      }, 2000);
   }
}
