import React, { Component } from 'react';
import {SERVICE_BASE_URL, STATUS_RELATIVE_URL} from './AlarmConstants.jsx';



/**
 * Component Alarm Status with checkbox on/off 
 */
class AlarmStatus extends Component {
    constructor() {
        super();
        this.state = {
                alarmStatus : false
        }
    }
    
    componentDidMount() {
        let url = SERVICE_BASE_URL + STATUS_RELATIVE_URL;
        //let stat = false;
        //call rest service
        fetch(url)
        .then(results => 
            {
                //TODO change to json and results.json();!!!!
                this.setState({alarmStatus : results.ok});
                console.log('toto je co vraci sluzba:  %s', results.ok);
            })
        .catch('error occured');
        
    }
    
    render() {
        //TODO
        console.log('statussssss: ' + this.state.alarmStatus);
        return (
                
                    <tr>
                        <th>Alarm Status</th><th>{this.state.alarmStatus ? 'Je tam' : 'neni tam'}</th>
                    </tr>
                
                );
    }
}

export default AlarmStatus;