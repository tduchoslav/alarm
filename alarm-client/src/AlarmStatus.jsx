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
        var request = new Request(url, {
            headers: new Headers({
                'Content-Type': 'text/plain',
                'accept': 'application/json'   
            })
        });
        fetch(request)
        .then(results => {
            let json = results.json();
            console.log('results:  %s', json);
            return json;
            })
        .then(jsonData => 
            {
                this.setState({alarmStatus : jsonData['enabled']});
            })
        .catch(e => {console.error('my error occured during fetching: %s got error %s', url, e)});
        
    }
    
    render() {
        //TODO
        return (
                
                    <tr>
                        <th>Alarm Status</th><th>{this.state.alarmStatus ? 'Je tam' : 'neni tam'}</th>
                    </tr>
                
                );
    }
}

export default AlarmStatus;