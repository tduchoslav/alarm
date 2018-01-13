import React, { Component } from 'react';
import {SERVICE_BASE_URL, ALARM_TEST_RELATIVE_URL} from './AlarmConstants.jsx';

class AlarmTestingButton extends Component {    
    constructor(props) {
        super(props);
        this.state = {
                ajaxCallResult: null
        }
        
        this.handleClick = this.handleClick.bind(this);
    }
    
    
    render() {
        console.debug('AlarmTestingButton render');
        let serviceResultSpan = null;
        if (this.state.ajaxCallResult != null) {
            serviceResultSpan = 
                                <span className = {this.state.ajaxCallResult ? 'App-rest-service-result-success' : 'App-rest-service-result-error'}>
                                    {this.state.ajaxCallResult ? 'success' : 'failure'}
                                </span>
        }
        return (
                
                    <tr>
                        <td>
                            <span></span>
                        </td>                        
                        <td>
                            <button onClick={this.handleClick} >Test</button>
                        </td>
                        <td>
                        </td>
                        <td>
                           {serviceResultSpan}
                        </td>
                    </tr>
                            
                );
    }
    
    handleClick(event) {
        let url = SERVICE_BASE_URL + ALARM_TEST_RELATIVE_URL;
        let request = new Request(url, {
            headers: new Headers({
                'Content-Type': 'text/plain',
                'accept': 'application/json'   
            })
        });
        fetch(request)
        .then(results => {
            let json = results.json();
            console.debug('results:  %s', json);
            return json;
            })
        .then(jsonData => 
            {
                this.setState({
                    ajaxCallResult : true
                });
            })
        .catch(
                e => {
                        console.error('my error occured during fetching: %s got error %', request, e);
                        this.setState({ajaxCallResult : false});
                    }                
               );
    }
}


export default AlarmTestingButton;